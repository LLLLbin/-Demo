package cn.lbin.miaosha.service;

import cn.lbin.miaosha.dao.OrderDao;
import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.domain.OrderInfo;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;


    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userid, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdGoodsId(userid,goodsId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        Long orderId = orderDao.insert(orderInfo);

        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setGoodsId(goodsVo.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        return orderInfo;
    }
}
