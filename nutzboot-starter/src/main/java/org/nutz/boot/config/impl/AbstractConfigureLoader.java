package org.nutz.boot.config.impl;

import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.aware.EnvHolderAware;
import org.nutz.boot.aware.ResourceLoaderAware;
import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.env.EnvHolder;
import org.nutz.boot.resource.ResourceLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.util.LifeCycle;

public abstract class AbstractConfigureLoader implements ConfigureLoader, ClassLoaderAware, LifeCycle, EnvHolderAware, ResourceLoaderAware {

    protected PropertiesProxy conf;
    protected ClassLoader classLoader;
    protected EnvHolder envHolder;
    protected ResourceLoader resourceLoader;

    public PropertiesProxy get() {
        return conf;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setEnvHolder(EnvHolder envHolder) {
        this.envHolder = envHolder;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public void fetch() throws Exception {}

    public void depose() throws Exception {}

}
