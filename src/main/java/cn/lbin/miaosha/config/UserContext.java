package cn.lbin.miaosha.config;

import cn.lbin.miaosha.domain.MiaoshaUser;

public class UserContext {
    private static ThreadLocal<MiaoshaUser> local=new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user){
        local.set(user);
    }

    public static MiaoshaUser getUser(){
        return local.get();
    }
}
