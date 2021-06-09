package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.dao.MiaoshaUserDao;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.redis.GoodsKey;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.service.GoodsService;
import cn.lbin.miaosha.service.MiaoshaUserService;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.vo.GoodsDetailVo;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller()
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

//    @RequestMapping("/to_list")
//    public String toList(Model model, MiaoshaUser user) {
//        model.addAttribute("user", user);
//        List<GoodsVo> list = goodsService.listGoodsVo();
//        model.addAttribute("goodsList", list);
//        return "goods_list";
//    }

    //使用页面级缓存
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user", user);

        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        List<GoodsVo> list = goodsService.listGoodsVo();
        model.addAttribute("goodsList", list);
        WebContext context = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());


        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }


//    @RequestMapping("/to_detail/{goodsId}")
//    public String detail(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId) {
//        model.addAttribute("user", user);
//
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goodsVo);
//
//        int miaoshaStatus = 0;
//        int remainSeconds = 0;
//
//        long startTime = goodsVo.getStartDate().getTime();
//        long endTime = goodsVo.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//        if (now < startTime) {
//            miaoshaStatus = 0;
//            remainSeconds = (int) ((startTime - now) / 1000);
//        } else if (now > endTime) {
//            miaoshaStatus = 2;
//            remainSeconds = -1;
//        } else {
//            miaoshaStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("miaoshaStatus", miaoshaStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";
//    }

    //使用静态化缓存数据
    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        String html = redisService.get(GoodsKey.getGoodsDetail, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        if (now < startTime) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startTime - now) / 1000);
        } else if (now > endTime) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        WebContext context = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "", html);
        }
        return html;
    }


    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public ResultEntity<GoodsDetailVo> detailInterface(HttpServletRequest request, HttpServletResponse response,
                                                       Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId) {
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        if (now < startTime) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startTime - now) / 1000);
        } else if (now > endTime) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo detailVo = new GoodsDetailVo();
        detailVo.setMiaoshaStatus(miaoshaStatus);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        return ResultEntity.successWithData(detailVo);
    }
}
