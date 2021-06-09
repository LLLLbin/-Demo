package cn.lbin.miaosha.vo;

import cn.lbin.miaosha.domain.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private OrderInfo orderInfo;
}
