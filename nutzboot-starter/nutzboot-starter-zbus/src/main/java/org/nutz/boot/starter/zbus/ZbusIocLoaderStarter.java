package org.nutz.boot.starter.zbus;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.zbus.ZbusIocLoader;
import org.nutz.ioc.IocLoader;

public class ZbusIocLoaderStarter implements IocLoaderProvider {
    
    public IocLoader getIocLoader() {
        return new ZbusIocLoader();
    }

}
