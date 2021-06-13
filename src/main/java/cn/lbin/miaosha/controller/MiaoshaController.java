package cn.lbin.miaosha.controller;

import cn.lbin.miaosha.annotation.AccessLimit;
import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.rabbitmq.MQProducer;
import cn.lbin.miaosha.rabbitmq.MiaoshaMessage;
import cn.lbin.miaosha.redis.*;
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

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    private Map<Long, Boolean> localOverMap = new HashMap<>();

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
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public ResultEntity<Integer> doMiaosha(Model model, MiaoshaUser user,
                                           @RequestParam("goodsId") long goodsId,
                                           @PathVariable("path") String path) {
        model.addAttribute("user", user);
        /** 判断用户是否已经登陆了 */
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
        /**验证path*/
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check) {
            return ResultEntity.failed(MsgConstant.REQUEST_ILLEGAL);
        }
        /**为秒杀是否结束添加标记，如果标记为true，那就直接退出*/
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return ResultEntity.failed(MsgConstant.MIAOSHA_OVER);
        }
        /** 判断库存是否充足 */
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
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
     */
    @AccessLimit(seconds = 5, maxCount = 10, needLogin = true)
    @GetMapping("/result")
    @ResponseBody
    public ResultEntity<Long> miaoshaResult(Model model, MiaoshaUser user,
                                            @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        /** 判断用户是否已经登陆了 */
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
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
            localOverMap.put(vo.getId(), false);
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public ResultEntity<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);

        miaoshaService.reset(goodsList);
        return ResultEntity.successWithData(true);
    }


    /**
     * 创建接口的随机路径
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public ResultEntity<String> getMiaoshaPath(HttpServletRequest request, Model model, MiaoshaUser user,
                                               @RequestParam("goodsId") long goodsId,
                                               @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        model.addAttribute("user", user);
        /** 判断用户是否已经登陆了 */
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
        /**判断输入的验证码是否正确**/
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return ResultEntity.failed(MsgConstant.REQUEST_ILLEGAL);
        }
        /**创建真正的秒杀接口路径**/
        String path = miaoshaService.createPath(user, goodsId);
        return ResultEntity.successWithData(path);
    }

    /**
     * 生成图片验证码
     */
    @RequestMapping("/verifyCode")
    @ResponseBody
    public ResultEntity createVerifyCode(HttpServletResponse response, MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return ResultEntity.failed(MsgConstant.MESSAGE_ACCESS_FORBIDDEN);
        }
//        Integer newGoodsId = Integer.valueOf(goodsId);
        BufferedImage image = miaoshaService.createaVerifyCode(user, goodsId);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultEntity.failed(MsgConstant.MIAOSHA_FAILED);
        }
    }
}
