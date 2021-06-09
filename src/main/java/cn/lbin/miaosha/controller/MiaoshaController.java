package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.domain.OrderInfo;
import cn.lbin.miaosha.service.GoodsService;
import cn.lbin.miaosha.service.MiaoshaService;
import cn.lbin.miaosha.service.OrderService;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

//    @RequestMapping("/do_miaosha")
//    public String doMiaosha(Model model, MiaoshaUser user,
//                            @RequestParam("goodsId") long goodsId) {
//        model.addAttribute("user", user);
//        //判断是否登陆了
//        if (user == null) {
//            return "login";
//        }
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//        //判断库存是否充足
//        int stock = goodsVo.getStockCount();
//        if (stock <= 0) {
//            model.addAttribute("errmsg", MsgConstant.MIAOSHA_OVER);
//            return "miaosha_fail";
//        }
//        //判断是否已经秒杀过该商品了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (order != null) {
//            model.addAttribute("errmsg",MsgConstant.MIAOSHA_REPEAT);
//            return "miaosha_fail";
//        }
//        //减库存、下订单、写入秒杀订单
//        OrderInfo orderInfo=miaoshaService.miaosha(user,goodsVo);
//        model.addAttribute("orderInfo",orderInfo);
//        model.addAttribute("goods",goodsVo);
//        return "order_detail";
//    }


    @PostMapping("/do_miaosha")
    @ResponseBody
    public ResultEntity<OrderInfo> doMiaosha(Model model, MiaoshaUser user,
                            @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        //判断是否登陆了
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断库存是否充足
        int stock = goodsVo.getStockCount();
        if (stock <= 0) {
            return ResultEntity.failed(MsgConstant.MIAOSHA_OVER);
        }
        //判断是否已经秒杀过该商品了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ResultEntity.failed(MsgConstant.MIAOSHA_REPEAT);
        }
        //减库存、下订单、写入秒杀订单
        OrderInfo orderInfo=miaoshaService.miaosha(user,goodsVo);
        return ResultEntity.successWithData(orderInfo);
    }
}
