package org.nutz.boot.config.impl;

import org.nutz.boot.AppContext;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.env.EnvHolder;
import org.nutz.boot.resource.ResourceLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.util.LifeCycle;

public abstract class AbstractConfigureLoader implements ConfigureLoader, LifeCycle, AppContextAware {

    protected PropertiesProxy conf = new PropertiesProxy();
    protected AppContext appContext;
    protected EnvHolder envHolder;
    protected ResourceLoader resourceLoader;
    protected String[] args;

    public PropertiesProxy get() {
        return conf;
    }
    
    public void setCommandLineProperties(String... args) {
    	this.args = args;
    }

    public void setAppContext(AppContext appContext) {
    	this.appContext = appContext;

    	envHolder = appContext.getEnvHolder();
    	resourceLoader = appContext.getResourceLoader();
    }
    
    protected void parseCommandLineArgs(PropertiesProxy conf, String[] args) {
    	for (int i=0;i< args.length;i++) {
    		String arg = args[i];
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
