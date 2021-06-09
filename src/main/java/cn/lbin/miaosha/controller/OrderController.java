package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.domain.OrderInfo;
import cn.lbin.miaosha.service.GoodsService;
import cn.lbin.miaosha.service.OrderService;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.vo.GoodsVo;
import cn.lbin.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResultEntity<OrderDetailVo> info(MiaoshaUser user, @RequestParam("orderId")long orderId){
        if (user==null){
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
        OrderInfo orderInfo=orderService.getOrderById(orderId);
        if (orderInfo==null){
            return ResultEntity.failed(MsgConstant.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoodsVo(goods);
        detailVo.setOrderInfo(orderInfo);
        return ResultEntity.successWithData(detailVo);
    }
}
