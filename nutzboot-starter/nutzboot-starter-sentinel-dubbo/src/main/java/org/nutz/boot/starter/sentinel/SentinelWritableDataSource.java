package org.nutz.boot.starter.sentinel;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import org.nutz.integration.jedis.RedisService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SentinelWritableDataSource<T> implements WritableDataSource<T> {
    private RedisService redisService;
    private String ruleKey;
    private final Converter<T, String> configEncoder;
    private final Lock lock = new ReentrantLock(true);


    public SentinelWritableDataSource(RedisService redisService, Converter<T, String> configEncoder, String ruleKey) {
        this.redisService = redisService;
        this.configEncoder = configEncoder;
        this.ruleKey = ruleKey;
    }

    @Override
    public void write(T value) throws Exception {
        lock.lock();
        try {
            String convertResult = configEncoder.convert(value);
            redisService.set(ruleKey, convertResult);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing
    }
}
