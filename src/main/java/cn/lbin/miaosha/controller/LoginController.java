package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.service.MiaoshaUserService;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.util.ValidateMobile;
import cn.lbin.miaosha.vo.LoginVo;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller()
public class LoginController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    private static Logger log= LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public ResultEntity doLogin(HttpServletResponse response,
                                @Valid LoginVo loginVo){
        log.info(loginVo.toString());
//        String mobile = loginVo.getMobile();
//        String password = loginVo.getPassword();
//        if (StringUtils.isEmpty(password)) {
//            return ResultEntity.failed(MsgConstant.MESSAGE_STRING_INVALIDATE);
//        }
//        if (!ValidateMobile.isMobile(mobile)){
//            return ResultEntity.failed(MsgConstant.MESSAGE_Mobile_INVALIDATE);
//        }
        miaoshaUserService.login(response,loginVo);
        return ResultEntity.successWithoutData();
    }
}
