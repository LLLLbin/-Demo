package cn.lbin.miaosha.vo;

import cn.lbin.miaosha.domain.MiaoshaUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoodsDetailVo {
    private int miaoshaStatus;
    private int remainSeconds;
    private GoodsVo goodsVo;
    private MiaoshaUser user;

}
