package org.nutz.boot.starter.caffeine;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@IocBean(create = "init")
public class CaffeineInterceptor implements MethodInterceptor {

    private static Log log = Logs.get();
    private static final ConcurrentMap<CacheStrategy, Cache<String, Object>> cacheMap = new ConcurrentHashMap<>();
    private static final Map<String, CacheStrategy> cacheStrategyMap = new HashMap<>();

    @Inject
    protected PropertiesProxy conf;

    protected KeyStringifier stringifier;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    private static Cache<String, Object> getCache(CacheStrategy strategy) {
        Cache<String, Object> cache = cacheMap.get(strategy);
        if (cache == null) {
            synchronized (strategy) {
                cache = cacheMap.get(strategy);
                if (cache == null) {
                    Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
                    if (strategy.getMaxIdle() > 0)
                        caffeine.expireAfterAccess(strategy.getMaxIdle(), TimeUnit.MILLISECONDS);
                    if (strategy.getMaxLive() > 0)
                        caffeine.expireAfterWrite(strategy.getMaxLive(), TimeUnit.MILLISECONDS);
                    if (strategy.getMaxSize() > 0)
                        caffeine.maximumSize(strategy.getMaxSize());
                    cache = caffeine.build();
                    cacheMap.put(strategy, cache);
                }
            }
        }
        return cache;
    }

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        if (method.getReturnType() == void.class) {
            log.warnf("method [%s] is void,should not use @Cache", method);
            chain.doChain();
            return;
        }
        String name = method.getAnnotation(org.nutz.boot.starter.caffeine.Cache.class).value();
        CacheStrategy strategy = cacheStrategyMap.get(name);
        if (strategy == null) {
            log.warnf("CacheStrategy[%s] on Method[%s] doesn't exist", name, method);
            chain.doChain();
            return;
        }
        Cache<String, Object> cache = getCache(strategy);
        String key = getKey(method, chain.getArgs());
        Object value = cache.getIfPresent(key);
        if (value == null) {
            chain.doChain();
            cache.put(key, chain.getReturn());
        } else
            chain.setReturnValue(value);
    }

    private String getKey(Method method, Object[] args) {
        String fullName = String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
        if (args == null || args.length == 0)
            return fullName;
        return fullName + Arrays.stream(args).map(stringifier::stringify).collect(Collectors.joining("$"));
    }

    public void init() {
        Map<String, CacheStrategy> map = new HashMap<>();
        String[] types = ioc.getNamesByType(KeyStringifier.class);
        if (Lang.isEmptyArray(types)) {
            this.stringifier = String::valueOf;
        } else {
            this.stringifier = ioc.get(KeyStringifier.class, types[0]);
        }
        log.debugf("use %s as KeyStringifier", this.stringifier);
        conf.entrySet().stream().filter(entry -> entry.getKey().startsWith(CaffeineStarter.PRE)).forEach(entry -> {
            if (entry.getValue() == null)
                return;
            String[] split = entry.getKey().substring(CaffeineStarter.PRE.length()).split("\\.");
            if (split.length == 2) {
                // cache.name=10000,-1,-1 这种
                try {
                    String[] values = entry.getValue().replace(" ", ",").split(",");
                    long maxSize = Long.parseLong(values[0].trim());
                    long maxIdle = values.length > 1 ? Long.parseLong(values[1].trim()) : 0l;
                    long maxLive = values.length > 2 ? Long.parseLong(values[2].trim()) : 0l;
                    CacheStrategy cacheStrategy = new CacheStrategy(split[1].trim(), maxSize, maxIdle, maxLive);
                    cacheStrategyMap.put(cacheStrategy.getName(), cacheStrategy);
                    log.debugf("load CacheStrategy %s", cacheStrategy);
                } catch (Exception e) {
                    log.errorf("failed to load cache [%s]", entry.getKey());
                }
            } else if (split.length == 3) {
                // cache.name.maxSize=10000
                try {
                    String type = split[2].trim();
                    long value = Long.parseLong(entry.getValue());
                    String name = split[1].trim();
                    CacheStrategy cacheStrategy = map.get(name);
                    if (cacheStrategy == null) {
                        cacheStrategy = new CacheStrategy(name);
                        map.put(name, cacheStrategy);
                    }
                    if ("maxSize".equalsIgnoreCase(type))
                        cacheStrategy.setMaxSize(value);
                    else if ("maxIdle".equalsIgnoreCase(type))
                        cacheStrategy.setMaxIdle(value);
                    else if ("maxLive".equalsIgnoreCase(type))
                        cacheStrategy.setMaxLive(value);
                } catch (Exception e) {
                    log.errorf("failed to apply cache rule [%s]", entry.getKey());
                }
            }
        });
        map.values().stream().forEach(cacheStrategy -> log.debugf("load CacheStrategy %s", cacheStrategy));
        cacheStrategyMap.putAll(map);
        if (!cacheStrategyMap.containsKey(CacheStrategy.DEFAULT)) {
            // 没有默认的话，加一个默认
            cacheStrategyMap.put(CacheStrategy.DEFAULT, new CacheStrategy(CacheStrategy.DEFAULT, 10000, 0, 0));
            log.debugf("load DEFAULT CacheStrategy %s", cacheStrategyMap.get(CacheStrategy.DEFAULT));
        }
    }

}
