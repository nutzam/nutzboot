package org.nutz.boot.starter.dubbo;

import org.nutz.boot.AppContext;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.dubbo.DubboIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

public class DubboIocLoaderStarter implements IocLoaderProvider, AppContextAware {

    protected PropertiesProxy conf;

    public IocLoader getIocLoader() {
        return new DubboIocLoader(Strings.splitIgnoreBlank(conf.get("dubbo.xmlPaths", "dubbo.xml")));
    }

    public void setAppContext(AppContext appContext) {
        conf = appContext.getConf();
    }
}
