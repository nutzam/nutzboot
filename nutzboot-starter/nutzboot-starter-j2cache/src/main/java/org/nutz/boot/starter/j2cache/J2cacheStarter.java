package org.nutz.boot.starter.j2cache;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 *
 * @Author 蛋蛋
 * @Time 2018年1月17日 20:20:35
 */
@IocBean
public class J2cacheStarter {
    @IocBean
    public CacheChannel getCacheChannel(){
        return J2Cache.getChannel();
    }
}
