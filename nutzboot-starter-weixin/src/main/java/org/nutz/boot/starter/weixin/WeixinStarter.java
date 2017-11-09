package org.nutz.boot.starter.weixin;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.ioc.IocLoader;
import org.nutz.plugins.weixin.WeixinIocLoader;

public class WeixinStarter implements IocLoaderProvider {

    public IocLoader getIocLoader() {
        return new WeixinIocLoader();
    }

}
