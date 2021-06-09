package cn.lbin.miaosha.rabbitmq;

import cn.lbin.miaosha.config.MQConfig;
import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.MiaoshaUser;
import cn.lbin.miaosha.redis.RedisService;
import cn.lbin.miaosha.service.GoodsService;
import cn.lbin.miaosha.service.MiaoshaService;
import cn.lbin.miaosha.service.OrderService;
import cn.lbin.miaosha.util.ResultEntity;
import cn.lbin.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQConsumer {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    private static Logger logger = LoggerFactory.getLogger(MQConsumer.class);

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        logger.info("receive message: " + message);
        MiaoshaMessage bean = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = bean.getUser();
        long goodsId = bean.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if (stock <= 0){
            return ;
        }
        /** 判断用户是否已经购买过该商品了 */
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ;
        }
        miaoshaService.miaosha(user,goods);
    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        logger.info("receive message: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        logger.info("receive topic queue1 message: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        logger.info("receive topic queue2 message: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeader(byte[] message){
//        logger.info("receive header queue message: "+new String(message));
//    }
}
