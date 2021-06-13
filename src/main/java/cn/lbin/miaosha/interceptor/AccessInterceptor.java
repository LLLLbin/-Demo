package cn.lbin.miaosha.interceptor;

import cn.lbin.miaosha.annotation.AccessLimit;
import cn.lbin.miaosha.config.UserContext;
import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.execption.GlobalException;
import cn.lbin.miaosha.redis.AccessKey;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.service.MiaoshaUserService;
import cn.lbin.miaosha.util.ResultEntity;
import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.objects.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            MiaoshaUser user = getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit limit = hm.getMethodAnnotation(AccessLimit.class);
            if (limit == null) {
                return true;
            }
            int seconds = limit.seconds();
            int maxCount = limit.maxCount();
            boolean login = limit.needLogin();

            String key = request.getRequestURI();
            if (login) {
                if (user == null) {
//                    render(response, MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
//                    return false;
                    throw new GlobalException(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
                }
                key += "_" + user.getId();
            }else {

            }

            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count < maxCount) {
                redisService.incr(ak, key);
            } else {
//                render(response, MsgConstant.ACCESS_LIMIT);
//                return false;
                throw new GlobalException(MsgConstant.ACCESS_LIMIT);
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, String message) throws IOException {
        ServletOutputStream stream = response.getOutputStream();
        String str= JSON.toJSONString(ResultEntity.failed(message));
        stream.write(str.getBytes("UTF-8"));
        stream.flush();
        stream.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String parameterToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(parameterToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(parameterToken) ? cookieToken : parameterToken;
        return userService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
