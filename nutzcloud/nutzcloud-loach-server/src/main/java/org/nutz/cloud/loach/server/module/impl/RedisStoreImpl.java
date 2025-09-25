package org.nutz.cloud.loach.server.module.impl;
import org.nutz.cloud.loach.server.module.Store;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.List;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

@IocBean(name="redisStore", create = "init")
public class RedisStoreImpl implements Store {

    @Inject
    protected PropertiesProxy conf;

    private int expire;

    public void init() {
        this.expire = getPingTimeout() / 1000;
    }

    public int getPingTimeout() {
        return conf.getInt("loach.server.ping.timeout", 15000);
    }

    @Override
    @Aop("redis")
    public Boolean has(String key) {
        long re = jedis().expire(key, expire);
        return re == 1;
    }

    @Override
    @Aop("redis")
    public void put(String key, String val) {
        jedis().setex(key, expire, val);
    }

    @Override
    @Aop("redis")
    public void del(String key) {
        jedis().del(key);
    }

    @Override
    @Aop("redis")
    public List<String> keys(String pattern) {
        return new ArrayList<>(jedis().keys(pattern));
    }

    @Override
    @Aop("redis")
    public String get(String key) {
        return jedis().get(key);
    }
}
