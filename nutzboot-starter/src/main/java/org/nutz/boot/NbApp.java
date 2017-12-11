package org.nutz.boot;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.aware.EnvHolderAware;
import org.nutz.boot.aware.IocAware;
import org.nutz.boot.aware.ResourceLoaderAware;
import org.nutz.boot.banner.SimpleBannerPrinter;
import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.config.impl.PropertiesConfigureLoader;
import org.nutz.boot.env.SystemPropertiesEnvHolder;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.boot.resource.ResourceLoader;
import org.nutz.boot.resource.impl.SimpleResourceLoader;
import org.nutz.boot.tools.PropDocReader;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.LogAdapter;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.IocBy;

/**
 * NutzBoot的主体
 * @author wendal
 *
 */
public class NbApp extends Thread {
    
	/**
	 *  日志属性要等日志适配器准备好了才能加载,这里不可以使用Logs.get();
	 */
    protected static Log log;
    
    /**
     * 命令行参数
     */
    protected String[] args;
    
    /**
     * 主启动器类,必须设置
     */
    protected Class<?> mainClass;
    
    /**
     * 是否允许命令行下的 -Dxxx.xxx.xxx=转为配置参数
     */
    protected boolean allowCommandLineProperties = true;
    
    /**
     * 是否打印配置文档
     */
    protected boolean printProcDoc;
    
    /**
     * 主上下文
     */
    protected AppContext ctx;
    
    /**
     * Starter共享的IocLoader
     */
    protected AnnotationIocLoader starterIocLoader;
    
    /**
     * Starter类列表
     */
    protected List<Class<?>> starterClasses;
    
    protected boolean prepared;
    
    protected Object lock = new Object();
    
    /**
     * 创建一个NbApp,把调用本构造方法的类作为mainClass
     */
    public NbApp() {
    	StackTraceElement[] ts = Thread.currentThread().getStackTrace();
    	if (ts.length > 2) {
    		setMainClass(Lang.loadClassQuite(ts[2].getClassName()));
    	}
    }
    
    /**
     * 基于mainClass创建一个NbApp
     * @param mainClass 主启动类
     */
    public NbApp(Class<?> mainClass) {
        this.mainClass = mainClass;
    }
    
    /**
     * 设置命令行参数,可选
     */
    public NbApp setArgs(String[] args) {
        this.args = args;
        return this;
    }
    
    /**
     * 设置主启动类
     */
    public NbApp setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }
    
    /**
     * 是否允许从命令行读取配置信息, 例如 java -Dnutz.ioc.async.poolSize=128 xxx.jar
     * @param flag 默认允许
     */
    public NbApp setAllowCommandLineProperties(boolean flag) {
        this.allowCommandLineProperties = flag;
        return this;
    }
    
    /**
     * 是否打印配置文档
     */
    public NbApp setPrintProcDoc(boolean printProcDoc) {
		this.printProcDoc = printProcDoc;
		return this;
	}
    
    /**
     * 获取全局上下文
     * @return
     */
    public AppContext getAppContext() {
		return ctx;
	}
    
    public void run() {
        try {
            _run();
        } catch (Throwable e) {
            Logs.get().error("something happen", e);
        }
    }
    
    /**
     * 启动整个NbApp
     */
    public void _run() throws Exception {
    	Stopwatch sw = Stopwatch.begin();

        // 各种预备操作
    	this.prepare();
    	
    	if (printProcDoc) {
    		PropDocReader docReader = new PropDocReader(ctx);
        	docReader.load();
        	Logs.get().info("Configure Manual:\r\n" + docReader.toMarkdown());
    	}
        
        // 依次启动
    	try {
            ctx.init();

            ctx.startServers();

            if (mainClass.getAnnotation(IocBean.class) != null)
                ctx.getIoc().get(mainClass);

            sw.stop();
            log.infof("NB started : %sms", sw.du());
            synchronized (lock) {
                lock.wait();
            }
    	}
    	catch (Throwable e) {
            log.error("something happen!!", e);
        }
        // 收尾
        ctx.stopServers();
        ctx.depose();
    }
    
    public void shutdown() {
        log.info("ok, shutting down ...");
        synchronized (lock) {
            lock.notify();
        }
    }
    
    /**
     * 执行预备操作
     */
    public void prepare() throws Exception {
    	if (prepared)
    		return;
        // 初始化上下文
    	this.prepareBasic();
        
        // 打印Banner,暂时不可配置具体的类
        new SimpleBannerPrinter().printBanner(ctx);
        
        // 配置信息要准备好
        this.prepareConfigureLoader();
        
        // 创建Ioc容器
        prepareIoc();
        
        // 加载各种starter
        prepareStarterClassList();
        
        // 生成Starter实例
        prepareStarterInstance();
        
        prepared = true;
    }
    
    public void prepareBasic() throws Exception {
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
    }

    public void prepareConfigureLoader() throws Exception {
        if (ctx.getConfigureLoader() == null) {
            String cnfLoader = ctx.getEnvHolder().get("nutz.boot.base.ConfigureLoader");
            ConfigureLoader configureLoader;
            if (Strings.isBlank(cnfLoader)) {
                configureLoader = new PropertiesConfigureLoader();
            } else {
                configureLoader = (ConfigureLoader) ctx.getClassLoader().loadClass(cnfLoader).newInstance(); 
            }
            configureLoader.setCommandLineProperties(allowCommandLineProperties, args);
            aware(configureLoader);
            ctx.setConfigureLoader(configureLoader);
            if (configureLoader instanceof LifeCycle)
                ((LifeCycle) configureLoader).init();
        }
    }
    
    public void prepareIoc() throws Exception {
        if (ctx.getComboIocLoader() == null) {
        	int asyncPoolSize = ctx.getConfigureLoader().get().getInt("nutz.ioc.async.poolSize", 64);
        	List<String> args = new ArrayList<>();
        	args.add("*js");
        	args.add("ioc/");
        	args.add("*tx");
        	args.add("*async");
        	args.add(""+asyncPoolSize);
        	args.add("*anno");
        	args.add(ctx.getPackage());
        	IocBy iocBy = ctx.getMainClass().getAnnotation(IocBy.class);
        	if (iocBy != null) {
        		String[] tmp = iocBy.args();
        		ArrayList<String> _args = new ArrayList<>();
        		for (int i=0;i<tmp.length;i++) {
        			if (tmp[i].startsWith("*")) {
        				if (!_args.isEmpty()) {
        					switch (_args.get(0)) {
        					case "*tx":
        					case "*async":
        					case "*anno":
        					case "*js":
        						break;
        					default:
        						args.addAll(_args);
        					}
    						_args.clear();
        				}
        			}
    				_args.add(tmp[i]);
        		}
        		if (_args.size() > 0) {
        			switch (_args.get(0)) {
					case "*tx":
					case "*async":
					case "*anno":
					case "*js":
						break;
					default:
						args.addAll(_args);
					}
        		}
        	}
            ctx.setComboIocLoader(new ComboIocLoader(args.toArray(new String[args.size()])));
        }
        // 用于加载Starter的IocLoader
        starterIocLoader = new AnnotationIocLoader(NbApp.class.getPackage().getName() + ".starter");
        ctx.getComboIocLoader().addLoader(starterIocLoader);
        if (ctx.getIoc() == null) {
            ctx.setIoc(new NutIoc(ctx.getComboIocLoader()));
        }
        // 把核心对象放进ioc容器
        if (!ctx.ioc.has("appContext")){
            Ioc2 ioc2 = (Ioc2)ctx.getIoc();
            ioc2.getIocContext().save("app", "appContext", new ObjectProxy(ctx));
            ioc2.getIocContext().save("app", "conf", new ObjectProxy(ctx.getConfigureLoader().get()));
        }
    }
    
    public void prepareStarterClassList() throws Exception {
    	starterClasses = new ArrayList<>();
    	HashSet<String> classNames = new HashSet<>();
        Enumeration<URL> _en = ctx.getClassLoader().getResources("META-INF/nutz/org.nutz.boot.starter.NbStarter");
        while (_en.hasMoreElements()) {
            URL url = _en.nextElement();
            log.debug("Found " + url);
            try (InputStream ins = url.openStream()) {
                InputStreamReader reader = new InputStreamReader(ins);
                String tmp = Streams.readAndClose(reader);
                if (!Strings.isBlank(tmp)) {
                    for (String _tmp : Strings.splitIgnoreBlank(tmp, "[\n]")) {
                    	String className = _tmp.trim();
                    	if (!classNames.add(className))
                    		continue;
                        Class<?> klass = ctx.getClassLoader().loadClass(className);
                        if (!klass.getPackage().getName().startsWith(NbApp.class.getPackage().getName()) && klass.getAnnotation(IocBean.class) != null) {
                            starterIocLoader.addClass(klass);
                        }
                        starterClasses.add(klass);
                    }
                }
            }
        }
    }
    
    public void prepareStarterInstance() {
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
    }
    
    protected void aware(Object obj) {
    	// 需要注入AppContext
        if (obj instanceof AppContextAware) {
            ((AppContextAware) obj).setAppContext(ctx);
        }
        // 需要注入ClassLoader
        if (obj instanceof ClassLoaderAware) {
            ((ClassLoaderAware) obj).setClassLoader(ctx.getClassLoader());
        }
        // 需要注入EnvHolder
        if (obj instanceof EnvHolderAware) {
            ((EnvHolderAware) obj).setEnvHolder(ctx.getEnvHolder());
        }
        // 需要注入ResourceLoader
        if (obj instanceof ResourceLoaderAware) {
            ((ResourceLoaderAware) obj).setResourceLoader(ctx.getResourceLoader());
        }
        // 需要注入Ioc
        if (obj instanceof IocAware) {
            ((IocAware) obj).setIoc(ctx.getIoc());
        }
    }

}
