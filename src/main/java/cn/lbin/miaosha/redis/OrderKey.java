package cn.lbin.miaosha.redis;

import com.sun.tools.corba.se.idl.constExpr.Or;

public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super(prefix);
    }

    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid =new OrderKey("ByUG");
}
