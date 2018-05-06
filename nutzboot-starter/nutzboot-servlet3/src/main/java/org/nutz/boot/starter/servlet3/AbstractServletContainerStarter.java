package org.nutz.boot.starter.servlet3;

import java.util.Arrays;
import java.util.List;

import org.nutz.boot.AppContext;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.aware.IocAware;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class AbstractServletContainerStarter implements ClassLoaderAware, IocAware, AppContextAware, LifeCycle {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    protected ClassLoader classLoader;
    protected Ioc ioc;
    protected AppContext appContext;

    protected abstract String getConfigurePrefix();

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    // --getConf---
    public int getPort() {
        try {
            return appContext.getServerPort(getConfigurePrefix() + "port");
        }
        catch (NoSuchMethodError e) {
            log.info("Please remove 'nutzboot-starter' dependency from pom.xml. https://github.com/nutzam/nutzboot/issues/93");
            return conf.getInt(getConfigurePrefix() + "port", 8080);
        }
    }

    public String getHost() {
        try {
            return appContext.getServerHost(getConfigurePrefix() + "host");
        }
        catch (NoSuchMethodError e) {
            log.info("Please remove 'nutzboot-starter' dependency from pom.xml. https://github.com/nutzam/nutzboot/issues/93");
            return conf.get(getConfigurePrefix() + "host", "0.0.0.0");
        }
    }

    public String getContextPath() {
        return conf.get(getConfigurePrefix() + "contextPath", "/");
    }

    public List<String> getResourcePaths() {
        String PROP_STATIC_PATH = getConfigurePrefix() + "staticPath";
        if (Strings.isBlank(conf.get(PROP_STATIC_PATH)) || "static".equals(conf.get(PROP_STATIC_PATH)) || "static/".equals(conf.get(PROP_STATIC_PATH)))
            return Arrays.asList("static/", "webapp/");
        return Arrays.asList(conf.get(PROP_STATIC_PATH), "static/", "webapp/");
    }

    public void fetch() throws Exception {}

    public void depose() throws Exception {}

    public int getSessionTimeout() {
        return conf.getInt(getConfigurePrefix() + "session", conf.getInt("web.session.timeout", 30)) * 60;
    }
}
