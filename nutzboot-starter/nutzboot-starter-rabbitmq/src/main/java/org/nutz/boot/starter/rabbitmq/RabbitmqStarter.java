package org.nutz.boot.starter.rabbitmq;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.rabbitmq.RabbitmqIocLoader;
import org.nutz.ioc.IocLoader;

public class RabbitmqStarter implements IocLoaderProvider {
    
    public IocLoader getIocLoader() {
    	return new RabbitmqIocLoader();
    }
}
