package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.dao.MiaoshaUserDao;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller()
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @GetMapping("/to_list")
    public String toList(HttpServletResponse response, Model model,MiaoshaUser user)
                         {
        model.addAttribute("user", user);
        return "goods_list";
    }
}
