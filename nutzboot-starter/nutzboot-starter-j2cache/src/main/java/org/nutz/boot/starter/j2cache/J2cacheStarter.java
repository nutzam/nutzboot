package org.nutz.boot.starter.j2cache;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;

/**
 * @Author 蛋蛋(https://github.com/TopCoderMyDream)
 * @Time 2018年1月17日 20:20:35
 */
@IocBean
public class J2cacheStarter {
    private static final Log log = Logs.get();


    @IocBean
    public CacheChannel getCacheChannel()  {
        try {
            J2CacheConfig j2CacheConfig = J2CacheConfig.initFromConfig("/application.properties");
            return J2CacheBuilder.init(j2CacheConfig).getChannel();
        } catch (IOException e) {
            log.error("J2Cache star Fail,application.properties Non-existent!");
            e.printStackTrace();
        }
        return null;
    }
}
