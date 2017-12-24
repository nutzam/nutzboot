package org.nutz.boot;

import java.util.ArrayList;
import java.util.List;

import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.env.EnvHolder;
import org.nutz.boot.resource.ResourceLoader;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.util.LifeCycle;

public class AppContext implements LifeCycle {
    
    protected static AppContext _default = new AppContext();

    protected Ioc ioc;
    protected ClassLoader classLoader;
    protected ConfigureLoader configureLoader;
    protected ResourceLoader resourceLoader;
    protected EnvHolder envHolder;
    protected ComboIocLoader comboIocLoader;
    protected Class<?> mainClass;
    
    protected List<Object> starters = new ArrayList<>();
    
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
    
    public ConfigureLoader getConfigureLoader() {
        return configureLoader;
    }
    
    public PropertiesProxy getConf() {
        return configureLoader.get();
    }
    
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
    
    public EnvHolder getEnvHolder() {
        return envHolder;
    }
    
    public void setEnvHolder(EnvHolder envHolder) {
        this.envHolder = envHolder;
    }
    
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public void setConfigureLoader(ConfigureLoader configureLoader) {
        this.configureLoader = configureLoader;
    }
    
    public static AppContext getDefault() {
        return _default;
    }
    
    public static void setDefault(AppContext ctx) {
        _default = ctx;
    }
    
    public void setComboIocLoader(ComboIocLoader comboIocLoader) {
        this.comboIocLoader = comboIocLoader;
    }
    
    public ComboIocLoader getComboIocLoader() {
        return comboIocLoader;
    }
    
    public List<Object> getStarters() {
        return starters;
    }
    
    public void addStarter(Object obj) {
        starters.add(obj);
    }
    
    public Class<?> getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getPackage() {
    	return this.mainClass.getPackage().getName();
    }

    public void init() throws Exception {
        for (Object object : starters) {
            if (object instanceof LifeCycle)
                ((LifeCycle) object).init();
        }
    }

    public void fetch() throws Exception {
    }

    public void depose() throws Exception {
        for (Object object : starters) {
            if (object instanceof LifeCycle)
                ((LifeCycle) object).depose();
        }
        if (ioc != null)
            ioc.depose();
    }
    
    public void startServers() throws Exception {
        for (Object object : starters) {
            if (object instanceof ServerFace) {
                ((ServerFace) object).start();
            }
        }
    }
    
    public void stopServers() throws Exception {
        for (Object object : starters) {
            if (object instanceof ServerFace) {
                ((ServerFace) object).stop();
            }
        }
    }
    
    public <T> List<T> getBeans(Class<T> klass) {
        List<T> list = new ArrayList<>();
        for (String name : getIoc().getNamesByType(klass)) {
            if (name == null)
                continue;
            list.add(getIoc().get(klass, name));
        }
        return list;
    }
}
