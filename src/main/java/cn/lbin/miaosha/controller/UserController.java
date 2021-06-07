package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.util.ResultEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public ResultEntity getUserInfo(Model model, MiaoshaUser user){
        return ResultEntity.successWithData(user);
    }
}
