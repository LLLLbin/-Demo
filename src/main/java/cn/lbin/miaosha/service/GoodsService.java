package cn.lbin.miaosha.service;

import cn.lbin.miaosha.dao.GoodsDao;
import cn.lbin.miaosha.domain.Goods;
import cn.lbin.miaosha.domain.MiaoshaGoods;
import cn.lbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId)
    {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public void reduceStock(GoodsVo goodsVo) {
        MiaoshaGoods goods = new MiaoshaGoods();
        goods.setId(goodsVo.getId());
        goodsDao.reduceStock(goods);
    }
}
