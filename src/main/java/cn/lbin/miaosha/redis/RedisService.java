package cn.lbin.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisService {

    private Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    RedisConfig redisConfig;

    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            String s = jedis.get(realKey);
            T t = stringToBean(s, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(KeyPrefix keyPrefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            String str = beanToString(value);
            if (str == null || str.length() < 0) {
                return false;
            }
            int seconds = keyPrefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean exists(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            Long result = jedis.del(realKey);
            return result > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> Long incr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> Long decr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> aClass = value.getClass();
        if (aClass == int.class || aClass == Integer.class) {
            return String.valueOf(value);
        } else if (aClass == String.class) {
            return (String) value;
        } else if (aClass == long.class || aClass == Long.class) {
            return String.valueOf(value);
        }
        return JSON.toJSONString(value);
    }

    private <T> T stringToBean(String s, Class<T> aClass) {
        if (s == null || s.length() <= 0 || aClass == null) {
            return null;
        }
        if (aClass == int.class || aClass == Integer.class) {
            return (T) Integer.valueOf(s);
        } else if (aClass == String.class) {
            return (T) s;
        } else if (aClass == long.class || aClass == Long.class) {
            return (T) Long.valueOf(s);
        }
        return JSON.toJavaObject(JSON.parseObject(s), aClass);
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        JedisPool pool = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort()
                , redisConfig.getTimeout(), redisConfig.getPassword(), 0);
        return pool;
    }
}
