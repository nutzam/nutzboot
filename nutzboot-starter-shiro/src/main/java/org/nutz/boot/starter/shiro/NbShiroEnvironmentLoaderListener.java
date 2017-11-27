package org.nutz.boot.starter.shiro;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.ShiroException;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.util.ClassUtils;
import org.apache.shiro.util.UnknownClassException;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.nutz.boot.AppContext;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;

public class NbShiroEnvironmentLoaderListener extends EnvironmentLoaderListener {
	
	protected PropertiesProxy conf;
	
	protected AppContext appContext;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		PropertiesProxy conf = appContext.getConfigureLoader().get();
		try {
			// 走原生API的shiro.ini文件吗?
			String iniPath = conf.get("shiro.ini.path", "shiro.ini");
			if (conf.has("shiro.ini.path") || appContext.getResourceLoader().has(iniPath)) {
				sce.getServletContext().setAttribute(EnvironmentLoader.CONFIG_LOCATIONS_PARAM, iniPath);
				super.contextInitialized(sce);
				return;
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		sce.getServletContext().setAttribute(ENVIRONMENT_CLASS_PARAM, NbResourceBasedWebEnvironment.class.getName());
		super.contextInitialized(sce);
	}
	
    protected Class<?> determineWebEnvironmentClass(ServletContext servletContext) {
        String className = servletContext.getInitParameter(ENVIRONMENT_CLASS_PARAM);
        if (className != null) {
            try {
                return ClassUtils.forName(className);
            } catch (UnknownClassException ex) {
                throw new ConfigurationException(
                        "Failed to load custom WebEnvironment class [" + className + "]", ex);
            }
        } else {
        	try {
				String iniPath = conf.get("shiro.ini.path", "shiro.ini");
				if (conf.has("shiro.ini.path") || appContext.getResourceLoader().has(iniPath)) {
					return IniWebEnvironment.class;
				}
			} catch (IOException e) {
				throw new ShiroException(e);
			}
            return NbResourceBasedWebEnvironment.class;
        }
    }
}
