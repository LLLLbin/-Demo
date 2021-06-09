package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.domain.OrderInfo;
import cn.lbin.miaosha.rabbitmq.MQProducer;
import cn.lbin.miaosha.rabbitmq.MiaoshaMessage;
import cn.lbin.miaosha.redis.GoodsKey;
import cn.lbin.miaosha.redis.MiaoshaKey;
import cn.lbin.miaosha.redis.OrderKey;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.service.GoodsService;
import cn.lbin.miaosha.service.MiaoshaService;
import cn.lbin.miaosha.service.OrderService;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQProducer producer;

    private Map<Long,Boolean> localOverMap=new HashMap<>();

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


    //普通秒杀方法，没有做redis预减、消息队列
//    @PostMapping("/do_miaosha")
//    @ResponseBody
//    public ResultEntity<OrderInfo> doMiaosha(Model model, MiaoshaUser user,
//                            @RequestParam("goodsId") long goodsId) {
//        model.addAttribute("user", user);
//        //判断是否登陆了
//        if (user == null) {
//            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
//        }
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//        //判断库存是否充足
//        int stock = goodsVo.getStockCount();
//        if (stock <= 0) {
//            return ResultEntity.failed(MsgConstant.MIAOSHA_OVER);
//        }
//        //判断是否已经秒杀过该商品了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (order != null) {
//            return ResultEntity.failed(MsgConstant.MIAOSHA_REPEAT);
//        }
//        //减库存、下订单、写入秒杀订单
//        OrderInfo orderInfo=miaoshaService.miaosha(user,goodsVo);
//        return ResultEntity.successWithData(orderInfo);
//    }

    //
    @PostMapping("/do_miaosha")
    @ResponseBody
    public ResultEntity<Integer> doMiaosha(Model model, MiaoshaUser user,
                                           @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);

        /** 判断用户是否已经登陆了 */
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }

        Boolean over = localOverMap.get(goodsId);
        if(over){
            return ResultEntity.failed(MsgConstant.MIAOSHA_OVER);
        }
        /** 判断库存是否充足 */
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId,true);
            return ResultEntity.failed(MsgConstant.MIAOSHA_OVER);
        }
        /** 判断用户是否已经购买过该商品了 */
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ResultEntity.failed(MsgConstant.MIAOSHA_REPEAT);
        }
        /** 进行入队操作 */
        MiaoshaMessage message = new MiaoshaMessage(user, goodsId);
        producer.sendMiaoshaMessage(message);
        return ResultEntity.successWithData(0);
    }


    /**
     * orderId : 成功
     * 1 : 秒杀失败
     * 0 : 排队中
     * */
    @GetMapping("/result")
    @ResponseBody
    public ResultEntity<Long> miaoshaResult(Model model, MiaoshaUser user,
                                               @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        /** 判断用户是否已经登陆了 */
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
        long result=miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return ResultEntity.successWithData(result);
    }


    /*
     * 系统初始化后，会调用该方法
     * */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodList = goodsService.listGoodsVo();
        if (goodList == null) {
            return;
        }
        for (GoodsVo vo : goodList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + vo.getId(), vo.getStockCount());
            localOverMap.put(vo.getId(),false);
        }
    }

    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public ResultEntity<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);

        miaoshaService.reset(goodsList);
        return ResultEntity.successWithData(true);
    }


}
