package cn.lbin.miaosha.redis;

public class MiaoshaKey extends BasePrefix{
    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver =new MiaoshaKey("go");
}
