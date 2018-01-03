package org.nutz.boot.starter.beetl;

import java.io.IOException;
import java.util.Properties;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.nutz.BeetlViewMaker;
import org.beetl.ext.nutz.LogErrorHandler;
import org.beetl.ext.web.WebRender;
import org.nutz.boot.AppContext;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(name="$views_beetl", create="init")
public class BeetlViewMakerStarter extends BeetlViewMaker {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

	public BeetlViewMakerStarter() throws IOException {
		super();
	}

    public void init() throws IOException {
    	if (conf == null)
    		return;
        log.debug("beetl init ....");
        Properties prop = new Properties();
        for (String key : conf.keySet()) {
        	if (key.startsWith("beetl.")) {
        		prop.put(key.substring("beetl.".length()), conf.get(key));
        	}
        }
        Configuration cfg = new Configuration(prop);
        if (!prop.containsKey(Configuration.DIRECT_BYTE_OUTPUT)) {
            // 默认启用DIRECT_BYTE_OUTPUT,除非用户自定义, 一般不会.
            log.debug("no custom DIRECT_BYTE_OUTPUT found , set to true");
            // 当DIRECT_BYTE_OUTPUT为真时, beetl渲染会通过getOutputStream获取输出流
            // 而BeetlView会使用LazyResponseWrapper代理getOutputStream方法
            // 从而实现在模板输出之前,避免真正调用getOutputStream
            // 这样@Fail视图就能正常工作了
            cfg.setDirectByteOutput(true);
        }
        if (!prop.containsKey(Configuration.ERROR_HANDLER)) {
            // 没有自定义ERROR_HANDLER,用定制的
            cfg.setErrorHandlerClass(LogErrorHandler.class.getName());
        }
        groupTemplate = new GroupTemplate(cfg);

        if (!prop.containsKey(Configuration.RESOURCE_LOADER)) {
            // 默认选用WebAppResourceLoader,除非用户自定义了RESOURCE_LOADER
            log.debug("no custom RESOURCE_LOADER found , select ClasspathResourceLoader");
            ClasspathResourceLoader loader = new ClasspathResourceLoader(appContext.getClassLoader(), prop.getProperty("root", "template/"));
            loader.setAutoCheck(true);
            groupTemplate.setResourceLoader(loader);
        }
        render = new WebRender(groupTemplate);
        log.debug("beetl init complete");
    }
}
