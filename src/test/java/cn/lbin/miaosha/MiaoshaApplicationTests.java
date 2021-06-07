package cn.lbin.miaosha;

import cn.lbin.miaosha.service.GoodsService;
import cn.lbin.miaosha.vo.GoodsVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    GoodsService goodsService;

    @Test
    void getGoodsListTest() {
        List<GoodsVo> list = goodsService.listGoodsVo();
//        System.out.println(list.get(0).getMiaoshaPrice());
        for (GoodsVo vo : list) {
            System.out.println(vo.getGoodsImg());
        }
    }

    @Test
    void getGoodsVoByGoodsId() {
//        System.out.println(goodsService.getGoodsVoByGoodsId(1));
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(1);
        System.out.println(goods.getGoodsStock());
    }

    public void getGoodsVoByGoodsIdTest(){

    }
}
