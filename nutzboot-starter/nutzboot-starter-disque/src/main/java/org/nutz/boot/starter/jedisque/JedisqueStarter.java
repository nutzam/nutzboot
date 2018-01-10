package org.nutz.boot.starter.jedisque;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.jedisque.JedisqueIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class JedisqueStarter implements IocLoaderProvider {
    
    public IocLoader getIocLoader() {
    	return new JedisqueIocLoader();
    }
}
