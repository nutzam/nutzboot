package org.nutz.boot.starter.shiro;

import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.ShiroException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.env.ResourceBasedWebEnvironment;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.nutz.boot.AppContext;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NbResourceBasedWebEnvironment extends ResourceBasedWebEnvironment implements Initializable, Destroyable {

    private static final Log log = Logs.get();

    protected AppContext appContext;
    protected Ioc ioc;
    protected PropertiesProxy conf;

    public void init() throws ShiroException {
        appContext = AppContext.getDefault();
        ioc = appContext.getIoc();
        conf = appContext.getConfigureLoader().get();
        configure();
    }

    protected void configure() {
        this.objects.clear();

        WebSecurityManager securityManager = createWebSecurityManager();
        setWebSecurityManager(securityManager);
        String loginUrl = conf.get(ShiroEnvStarter.PROP_URL_LOGIN, "/user/login");
        String unauthorizedUrl = conf.get(ShiroEnvStarter.PROP_URL_UNAUTH, "/user/login");
        String logoutUrl = conf.get(ShiroEnvStarter.PROP_URL_LOGOUT_REDIRECT, "/");
        for (Map.Entry<String, Filter> en : DefaultFilter.createInstanceMap(null).entrySet()) {
            Filter filter = en.getValue();
            if (filter instanceof LogoutFilter) {
                ((LogoutFilter)filter).setRedirectUrl(logoutUrl);
            }
            else if (filter instanceof AuthenticatingFilter) {
                ((AuthenticatingFilter)filter).setLoginUrl(loginUrl);
            }
            else if (filter instanceof AccessControlFilter) {
                ((AccessControlFilter)filter).setLoginUrl(unauthorizedUrl);
            }
            objects.put(en.getKey(), en.getValue());
        }
        for (String objectName : Strings.splitIgnoreBlank(conf.get("shiro.objects", ""))) {
            objects.put(objectName, ioc.get(null, objectName));
        }

        FilterChainResolver resolver = createFilterChainResolver();
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }
        NutShiro.DefaultLoginURL = loginUrl;
        NutShiro.DefaultNoAuthURL = unauthorizedUrl;
    }

    public FilterChainResolver createFilterChainResolver() {
        String iniUrls = "[urls]\r\n" + conf.get(ShiroEnvStarter.PROP_INIT_URLS, "").trim();
        log.debug("shiro ini urls  ---> \r\n" + iniUrls);
        Ini ini = new Ini();
        ini.load(iniUrls);
        IniFilterChainResolverFactory resolverFactory = new IniFilterChainResolverFactory(ini, objects);
        return resolverFactory.getInstance();
    }

    protected WebSecurityManager createWebSecurityManager() {
        return ioc.get(WebSecurityManager.class, "shiroWebSecurityManager");
    }

}
