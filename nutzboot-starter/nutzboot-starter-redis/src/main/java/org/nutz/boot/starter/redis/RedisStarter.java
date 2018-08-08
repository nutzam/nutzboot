package org.nutz.boot.starter.redis;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * redis注入类
 *
 * @author wentao
 * @email wentao0291@gmail.com
 * @date 2018-08-08 21:49
 */
@IocBean
public class RedisStarter {

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @IocBean(name = "redis")
    public Redis createRedis() {
        // 扫描所有redisName
        String regex = "redis\\.(\\w*)\\.host\\.";
        for (String key : conf.getKeys()) {
            Pattern pattern = Regex.getPattern(regex);
            Matcher match = pattern.matcher(key);
            if (match.find()) {
                // 获取redisName
                String redisName = match.group(1);
                if(!redisName.equals("local")) { // local会在最后默认处理
                    Redis redis = new Redis(redisName, conf);
                    ioc.addBean(redisName + "Redis", redis);
                }
            }
        }
        return new Redis("local", conf); // 注入默认redis，如果没有配置local，但用户使用了默认redis，则会抛出异常 throw Lang.makeThrow("未连接到Redis，请检查配置项");
    }
}
