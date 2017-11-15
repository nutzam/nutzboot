package org.nutz.boot.starter.nutz.wkcache;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.ioc.IocLoader;
import org.nutz.plugins.wkcache.WkcacheIocLoader;

public class WkCacheStarter implements IocLoaderProvider {

    public IocLoader getIocLoader() {
        return new WkcacheIocLoader();
    }

}
