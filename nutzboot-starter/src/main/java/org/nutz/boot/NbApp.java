package org.nutz.boot;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.aware.EnvHolderAware;
import org.nutz.boot.aware.IocAware;
import org.nutz.boot.aware.ResourceLoaderAware;
import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.config.impl.PropertiesConfigureLoader;
import org.nutz.boot.env.SystemPropertiesEnvHolder;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.boot.resource.ResourceLoader;
import org.nutz.boot.resource.impl.SimpleResourceLoader;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.log.Logs;

public class NbApp {
    
    protected static Log log;
    
    protected String[] args;
    
    protected Class<?> mainClass;
    
    protected boolean allowCommandLineProperties;
    
    protected AppContext ctx;
    
    public NbApp() {
    }
    
    public NbApp(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    public NbApp setArgs(String[] args) {
        this.args = args;
        return this;
    }
    
    public NbApp setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }
    
    public NbApp setAllowCommandLineProperties(boolean flag) {
        this.allowCommandLineProperties = flag;
        return this;
    }
    
    public void run() throws Exception {
        // 就是NB
        // 初始化上下文
        if (this.ctx == null) {
            ctx = AppContext.getDefault();
        }
        if (ctx.getMainClass() == null && mainClass != null)
            ctx.setMainClass(mainClass);
        // 检查ClassLoader的情况
        if (ctx.getClassLoader() == null)
            ctx.setClassLoader(NbApp.class.getClassLoader());
        
        if (ctx.getEnvHolder() == null) {
            ctx.setEnvHolder(new SystemPropertiesEnvHolder());
        }
        
        // 看看日志应该用哪个
        String logAdapter = ctx.getEnvHolder().get("nutz.boot.base.LogAdapter");
        if (!Strings.isBlank(logAdapter)) {
            Logs.setAdapter((LogAdapter) ctx.getClassLoader().loadClass(logAdapter).newInstance());
        }
        log = Logs.get();
        
        // 资源加载器
        if (ctx.getResourceLoader() == null) {
            ResourceLoader resourceLoader = new SimpleResourceLoader();
            aware(resourceLoader);
            ctx.setResourceLoader(resourceLoader);
        }
        
        // 配置信息要准备好
        if (ctx.getConfigureLoader() == null) {
            String cnfLoader = ctx.getEnvHolder().get("nutz.boot.base.ConfigureLoader");
            ConfigureLoader configureLoader;
            if (Strings.isBlank(cnfLoader)) {
                configureLoader = new PropertiesConfigureLoader();
            } else {
                configureLoader = (ConfigureLoader) ctx.getClassLoader().loadClass(cnfLoader).newInstance(); 
            }
            aware(configureLoader);
            ctx.setConfigureLoader(configureLoader);
            if (configureLoader instanceof LifeCycle)
                ((LifeCycle) configureLoader).init();
        }
        
        // 创建Ioc容器
        if (ctx.getComboIocLoader() == null) {
            ctx.setComboIocLoader(new ComboIocLoader("*tx", "*async", ctx.getConfigureLoader().get().get("nutz.ioc.async.poolSize", "64")));
        }
        // 用于加载Starter的IocLoader
        AnnotationIocLoader starterIocLoader = new AnnotationIocLoader();
        ctx.getComboIocLoader().addLoader(starterIocLoader);
        if (ctx.getIoc() == null) {
            ctx.setIoc(new NutIoc(ctx.getComboIocLoader()));
        }
        // 把核心对象放进ioc容器
        {
            Ioc2 ioc2 = (Ioc2)ctx.getIoc();
            ioc2.getIocContext().save("app", "appContext", new ObjectProxy(ctx));
            ioc2.getIocContext().save("app", "conf", new ObjectProxy(ctx.getConfigureLoader().get()));
        }
        
        if (mainClass != null) {
            ctx.getComboIocLoader().addLoader(new AnnotationIocLoader(mainClass.getPackage().getName()));
        }
        
        // 加载各种starter
        List<Class<?>> starterClasses = new ArrayList<>();
        Enumeration<URL> _en = ctx.getClassLoader().getResources("META-INF/nutz/org.nutz.boot.starter.NbStarter");
        while (_en.hasMoreElements()) {
            URL url = _en.nextElement();
            log.debug("Found " + url);
            try (InputStream ins = url.openStream()) {
                InputStreamReader reader = new InputStreamReader(ins);
                String tmp = Streams.readAndClose(reader);
                if (!Strings.isBlank(tmp)) {
                    for (String _tmp : Strings.splitIgnoreBlank(tmp, "[\n]")) {
                        Class<?> klass = ctx.getClassLoader().loadClass(_tmp.trim());
                        if (klass.getAnnotation(IocBean.class) != null) {
                            starterIocLoader.addClass(klass);
                        }
                        starterClasses.add(klass);
                    }
                }
            }
        }
        
        // 生成Starter实例
        for (Class<?> klass : starterClasses) {
            Object obj;
            if (klass.getAnnotation(IocBean.class) == null) {
               obj = Mirror.me(klass).born();
            }
            else {
                obj = ctx.getIoc().get(klass);
            }
            aware(obj);
            if (obj instanceof IocLoaderProvider) {
                IocLoader loader = ((IocLoaderProvider) obj).getIocLoader();
                ctx.getComboIocLoader().addLoader(loader);
            }
            ctx.addStarter(obj);
        }
        
        // 排序各种starter
        // TODO 排序各种starter
        
        // 依次启动
        ctx.init();
        
        ctx.startServers();
        
        // 等待关闭
        Lang.quiteSleep(Integer.MAX_VALUE);
        
        // 收尾
        ctx.stopServers();
        ctx.depose();
    }

    protected void aware(Object obj) {
        if (obj instanceof AppContextAware) {
            ((AppContextAware) obj).setAppContext(ctx);
        }
        if (obj instanceof ClassLoaderAware) {
            ((ClassLoaderAware) obj).setClassLoader(ctx.getClassLoader());
        }
        if (obj instanceof EnvHolderAware) {
            ((EnvHolderAware) obj).setEnvHolder(ctx.getEnvHolder());
        }
        if (obj instanceof ResourceLoaderAware) {
            ((ResourceLoaderAware) obj).setResourceLoader(ctx.getResourceLoader());
        }
        if (obj instanceof IocAware) {
            ((IocAware) obj).setIoc(ctx.getIoc());
        }
    }
    
    public static void main(String[] args) throws Exception {
        new NbApp().setArgs(args).setMainClass(NbApp.class).run();
    }

}
