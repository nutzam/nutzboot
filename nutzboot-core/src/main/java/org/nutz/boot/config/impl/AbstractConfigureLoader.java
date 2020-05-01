package org.nutz.boot.config.impl;

import org.nutz.boot.AppContext;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.env.EnvHolder;
import org.nutz.boot.resource.ResourceLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.util.LifeCycle;

/**
 * 封装配置加载器的通用逻辑
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class AbstractConfigureLoader implements ConfigureLoader, LifeCycle, AppContextAware {

    /**
     * 配置对象
     */
    protected PropertiesProxy conf = new PropertiesProxy();
    /**
     * 全局上下文
     */
    protected AppContext appContext;
    /**
     * 环境加载器
     */
    protected EnvHolder envHolder;
    /**
     * 资源加载器
     */
    protected ResourceLoader resourceLoader;
    /**
     * 命令行参数
     */
    protected String[] args;
    /**
     * 是否读取命令行参数
     */
    protected boolean allowCommandLineProperties;

    /**
     * 获取最常用的PropertiesProxy实例
     */
    public PropertiesProxy get() {
        return conf;
    }
    
    /**
     * 设置命令行参数
     */
    public void setCommandLineProperties(boolean allowCommandLineProperties, String... args) {
    	this.args = args;
    	this.allowCommandLineProperties = allowCommandLineProperties;
    }

    /**
     * 设置AppContext
     */
    public void setAppContext(AppContext appContext) {
    	this.appContext = appContext;

    	envHolder = appContext.getEnvHolder();
    	resourceLoader = appContext.getResourceLoader();
    }
    
    /**
     * 处理命令行参数
     */
    protected void parseCommandLineArgs(PropertiesProxy conf, String[] args) {
    	for (int i=0;i< args.length;i++) {
            String arg = args[i];
            if (arg.startsWith("-D")) {
                String[] tmp = arg.split("=");
                addCommandLineArg(conf, tmp[0].substring(2), tmp.length == 1 ? "true" : tmp[1]);
                continue;
            }
    		if (!arg.startsWith("--")) {
    			continue;
    		}
    		// 是不是最后一个参数
    		if (i == args.length - 1) {
    			addCommandLineArg(conf, arg.substring(2), "true");
    		}
    		else if (args[i+1].startsWith("--")) {
    			addCommandLineArg(conf, arg.substring(2), "true");
    		}
    		else {
    			addCommandLineArg(conf, arg.substring(2), args[i+1]);
    			i++;
    		}
    	}
    }
    
    /**
     * 添加命令行参数
     */
    protected void addCommandLineArg(PropertiesProxy conf, final String key, String value) {
    	int index = key.indexOf('=');
    	String _key = key;
    	if (index > 0) {
    		_key = key.substring(0, index);
    		if (index == key.length() - 1) {
    			value = "";
    		}
    		else {
    			value = key.substring(index + 1);
    		}
    	}
    	if (value == null)
    		value = "true";
    	conf.put(_key.trim().intern(), value.trim().intern());
    }
    
    public void fetch() throws Exception {}

    public void depose() throws Exception {}

}
