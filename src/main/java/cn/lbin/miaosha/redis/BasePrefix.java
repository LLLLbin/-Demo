package cn.lbin.miaosha.redis;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class BasePrefix implements KeyPrefix {
    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix) {
        this.expireSeconds=0;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String name = getClass().getSimpleName();
        return name + ":" + prefix;
    }
}
