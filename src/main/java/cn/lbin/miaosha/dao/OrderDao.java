package cn.lbin.miaosha.dao;

import cn.lbin.miaosha.domain.MiaoshaOrder;
import cn.lbin.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userId} and " +
            "goods_id=#{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userid,
                                                @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id,goods_id,delivery_addr_id,good_name,goods_count," +
            "goods_price,order_channel,status,create_date) values(#{userId},#{goodsId},#{deliveryAddrId}," +
            "#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,
            before = false,statement = "select last_insert_id()")
    Long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order(user_id,order_id,goods_id) values(#{userId},#{orderId},#{goodsId})")
    void insertMiaoshaOrder(MiaoshaOrder orderInfo);

    @Select("select * from order_info where id =#{orderId}")
    OrderInfo getOrderById(long orderId);

    @Delete("delete from order_info")
    public void deleteOrders();

    @Delete("delete from miaosha_order")
    public void deleteMiaoshaOrders();
}
