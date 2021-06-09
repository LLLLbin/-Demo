package cn.lbin.miaosha.service;

import cn.lbin.miaosha.dao.GoodsDao;
import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.domain.OrderInfo;
import cn.lbin.miaosha.redis.MiaoshaKey;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        boolean flag = goodsService.reduceStock(goodsVo);
        if (flag){
            return orderService.createOrder(user,goodsVo);
        }else {
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }


    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (order!=null){
            return order.getOrderId();
        }else {
            boolean isOver=getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }


    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }
}
