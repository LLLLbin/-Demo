package cn.lbin.miaosha.rabbitmq;

import cn.lbin.miaosha.domain.MiaoshaUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MiaoshaMessage {
    private MiaoshaUser user;
    private long goodsId;
}
