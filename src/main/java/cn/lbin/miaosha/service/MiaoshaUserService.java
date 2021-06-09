package cn.lbin.miaosha.service;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.dao.MiaoshaUserDao;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.execption.GlobalException;
import cn.lbin.miaosha.redis.MiaoshaUserKey;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.util.MD5Util;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.util.UUIDUtil;
import cn.lbin.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getUserById(Long id) {
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }
        user = miaoshaUserDao.getUserById(id);
        if (user != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, user);
        }
        return user;
//        return miaoshaUserDao.getUserById(id);
    }

    public boolean updatePassword(String token,long id,String formPass){
        MiaoshaUser user = getUserById(id);
        if (user==null){
            throw new GlobalException(MsgConstant.MESSAGE_MOBILE_NOT_EXISTS);
        }
        MiaoshaUser miaoshaUser = new MiaoshaUser();
        miaoshaUser.setId(id);
        miaoshaUser.setPassword(MD5Util.fromPassToDbPass(formPass,user.getSalt()));
        miaoshaUserDao.update(miaoshaUser);

        redisService.delete(MiaoshaUserKey.getById,""+id);
        user.setPassword(miaoshaUser.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //获取用户
        MiaoshaUser user = getUserById(Long.valueOf(mobile));
        if (user == null) {
            throw new GlobalException(MsgConstant.MESSAGE_MOBILE_NOT_EXISTS);
        }
        //再次加密用户输入的密码，比对数据库中的密码是否一致
        String dbPass = MD5Util.fromPassToDbPass(password, user.getSalt());
        if (!dbPass.equals(user.getPassword())) {
            throw new GlobalException(MsgConstant.MESSAGE_LOGIN_FAILED);
        }
        //添加token
        String token = UUIDUtil.uuid();
        addCookie(response, user, token);
        return true;
    }

    public void addCookie(HttpServletResponse response, MiaoshaUser user, String token) {
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (user != null) {
            addCookie(response, user, token);
        }
        return user;
    }
}
