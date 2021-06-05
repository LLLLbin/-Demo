package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.User;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.redis.UserKey;
import cn.lbin.miaosha.service.UserService;
import cn.lbin.miaosha.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/thymeleaf")
    public String test(Model model){
        model.addAttribute("msg","LBin");
        return "hello";
    }

    @GetMapping("getUser/{id}")
    @ResponseBody
    public ResultEntity getUserById(@PathVariable("id") int id){
        User user = userService.getById(id);
        if (user!=null){
            return ResultEntity.successWithData(user);
        }else{
            return ResultEntity.failed(MsgConstant.ATTR_NAME_MESSAGE);
        }
    }

    @RequestMapping("/insert")
    @ResponseBody
    public ResultEntity insertUser(){
        Boolean tx = userService.tx();
        if (tx){
            return ResultEntity.successWithoutData();
        }else {
            return ResultEntity.failed(MsgConstant.ATTR_NAME_MESSAGE);
        }
    }

    @RequestMapping("/redisSet")
    @ResponseBody
    public ResultEntity redisSet(){
        User user = new User(1,"LBin");
        boolean b = redisService.set(UserKey.getById,"1", user);
        if (b==true){
            return ResultEntity.successWithoutData();
        }else {
            return ResultEntity.failed(MsgConstant.ATTR_NAME_MESSAGE);
        }
    }

    @RequestMapping("/redisGet")
    @ResponseBody
    public ResultEntity redisGet(){
        User user= redisService.get(UserKey.getById, "1", User.class);
        if (user!=null){
            return ResultEntity.successWithData(user);
        }else {
            return ResultEntity.failed(MsgConstant.ATTR_NAME_MESSAGE);
        }
    }
}
