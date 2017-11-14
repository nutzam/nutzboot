package org.nutz.boot.starter.jedis;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.jedis.JedisIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class JedisStarter implements IocLoaderProvider {
    
    public IocLoader getIocLoader() {
    	return new JedisIocLoader();
    }
}
