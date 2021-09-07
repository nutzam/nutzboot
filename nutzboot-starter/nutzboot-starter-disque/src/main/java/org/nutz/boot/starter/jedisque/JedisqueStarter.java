package org.nutz.boot.starter.jedisque;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.jedisque.JedisqueIocLoader;
import org.nutz.ioc.IocLoader;

public class JedisqueStarter implements IocLoaderProvider {
    
    public IocLoader getIocLoader() {
    	return new JedisqueIocLoader();
    }
}
