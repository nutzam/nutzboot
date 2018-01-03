package org.nutz.boot.starter.shiro;

import org.apache.shiro.ShiroException;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.apache.shiro.web.env.ResourceBasedWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

public class NbResourceBasedWebEnvironment extends ResourceBasedWebEnvironment implements Initializable, Destroyable {

	protected AppContext appContext;
	protected Ioc ioc;
	protected PropertiesProxy conf;
	
	public void init() throws ShiroException {
		appContext = ShiroUtil.appContext;
		ioc = appContext.getIoc();
		conf = appContext.getConfigureLoader().get();
		configure();
	}
	
	protected void configure() {
		this.objects.clear();

        WebSecurityManager securityManager = createWebSecurityManager();
        setWebSecurityManager(securityManager);

        FilterChainResolver resolver = createFilterChainResolver();
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }
        for (String objectName : Strings.splitIgnoreBlank(conf.get("shiro.objects", ""))) {
        	objects.put(objectName, ioc.get(null, objectName));
        }
	}

	public FilterChainResolver createFilterChainResolver() {
		return ioc.get(FilterChainResolver.class, "shiroFilterChainResolver");
	}

	protected WebSecurityManager createWebSecurityManager() {
		return ioc.get(WebSecurityManager.class, "shiroWebSecurityManager");
	}

}
