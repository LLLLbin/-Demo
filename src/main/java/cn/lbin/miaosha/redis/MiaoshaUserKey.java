package cn.lbin.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix {

    public static final int DEFAULT_EXPIRE=3600*24*2;

    public MiaoshaUserKey(String prefix) {
        super(prefix);
    }

    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(DEFAULT_EXPIRE,"tk");
}
