package org.nutz.boot;

import org.nutz.boot.config.ConfigureLoader;
import org.nutz.ioc.Ioc;
import org.nutz.lang.util.LifeCycle;

public class AppContext implements LifeCycle {
    
    protected static AppContext _default = new AppContext();

    protected Ioc ioc;
    protected ClassLoader classLoader;
    protected ConfigureLoader configure;
    
    public Ioc ioc() {
        return ioc;
    }
    
    public Ioc getIoc() {
        return ioc;
    }
    
    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public ConfigureLoader getConfigure() {
        return configure;
    }
    
    public void setConfigure(ConfigureLoader configure) {
        this.configure = configure;
    }
    
    public static AppContext getDefault() {
        return _default;
    }
    
    public static void setDefault(AppContext ctx) {
        _default = ctx;
    }

    public void init() throws Exception {
    }

    public void fetch() throws Exception {
    }

    public void depose() throws Exception {
    }
}
