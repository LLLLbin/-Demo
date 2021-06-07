package cn.lbin.miaosha.dao;

import cn.lbin.miaosha.domain.Goods;
import cn.lbin.miaosha.domain.MiaoshaGoods;
import cn.lbin.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

///img/iphonex.png
@Mapper
public interface GoodsDao {
    @Select("select g.*," +
            "mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date " +
            "from miaosha_goods mg " +
            "left join goods g on mg.goods_id=g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*," +
            "mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date " +
            "from miaosha_goods mg " +
            "left join goods g on mg.goods_id=g.id " +
            "where g.id=#{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update miaosha_goods set stock_count =stock_count-1 where " +
            "goods_id=#{id}")
    void reduceStock(MiaoshaGoods goods);
}
