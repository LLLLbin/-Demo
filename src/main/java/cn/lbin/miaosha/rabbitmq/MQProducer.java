package cn.lbin.miaosha.rabbitmq;

import cn.lbin.miaosha.config.MQConfig;
import cn.lbin.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MQProducer {
    private static Logger logger= LoggerFactory.getLogger(MQProducer.class);

    @Autowired
    AmqpTemplate amqpTemplate;


    public void sendMiaoshaMessage(MiaoshaMessage message) {
        String msg = RedisService.beanToString(message);
        logger.info("send message"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }


    /**
     * Direct模式
     * */
//    public void produce(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send message: "+msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//    }
//
//
//    public void produceTopic(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send topic message: "+msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY1,msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY2,msg+"2");
//    }
//
//
//    public void produceFanout(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send fanout message: "+msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+"2");
//    }
//
//
//    public void produceHander(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send fanout message: "+msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1","value1");
//        properties.setHeader("header2","value2");
//        Message obj=new Message(msg.getBytes(),properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
//    }

}
