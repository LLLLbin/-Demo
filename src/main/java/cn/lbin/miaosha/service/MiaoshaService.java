package cn.lbin.miaosha.service;

import cn.lbin.miaosha.dao.GoodsDao;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.domain.OrderInfo;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        goodsService.reduceStock(goodsVo);
        return orderService.createOrder(user,goodsVo);
    }
}
