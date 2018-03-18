package org.nutz.boot.starter.shiro;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.shiro.SimplePrincipalSerializer;
import org.nutz.integration.shiro.UU32SessionIdGenerator;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.cache.impl.lcache.LCacheManager;
import org.nutz.plugins.cache.impl.redis.RedisCacheManager;

@IocBean
public class ShiroEnvStarter implements WebEventListenerFace {

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected PropertiesProxy conf;

    @PropDoc(value = "是否启用Shiro的Session管理", defaultValue = "true")
    public static final String PROP_SESSION_ENABLE = "shiro.session.enable";
    @PropDoc(value = "Cookie的name", defaultValue = "sid")
    public static final String PROP_SESSION_COOKIE_NAME = "shiro.session.cookie.name";
    @PropDoc(value = "Cookie的过期时间,单位:毫秒", defaultValue = "946080000")
    public static final String PROP_SESSION_COOKIE_MAXAGE = "shiro.session.cookie.maxAge";
    @PropDoc(value = "Cookie是否只读", defaultValue = "true")
    public static final String PROP_SESSION_COOKIE_HTTPONLY = "shiro.session.cookie.httpOnly";
    @PropDoc(value = "设置使用的缓存类型", defaultValue = "memory")
    public static final String PROP_SESSION_CACHE_TYPE = "shiro.session.cache.type";
    @PropDoc(value = "设置redis缓存的模式", defaultValue = "kv")
    public static final String PROP_SESSION_CACHE_REDIS_MODE = "shiro.session.cache.redis.mode";
    @PropDoc(value = "session持久化时redis的debug模式", defaultValue = "false")
    public static final String PROP_SESSION_CACHE_REDIS_DEBUG = "shiro.session.cache.redis.debug";
    @PropDoc(value = "redis缓存的过期时间", defaultValue = "-1")
    public static final String PROP_SESSION_CACHE_REDIS_TTL = "shiro.session.cache.redis.ttl";
    @PropDoc(value = "urls过滤清单")
    public static final String PROP_INIT_URLS = "shiro.ini.urls";
    @PropDoc(value = "shiro.ini的路径,如果shiro.ini存在,就会使用它,否则走NB的内部逻辑")
    public static final String PROP_INIT_PATH = "shiro.ini.path";

    @PropDoc(value = "默认登录路径", defaultValue = "/user/login")
    public static final String PROP_URL_LOGIN = "shiro.url.login";
    @PropDoc(value = "退出登录后的重定向路径", defaultValue = "/")
    public static final String PROP_URL_LOGOUT_REDIRECT = "shiro.url.logout_redirect";
    @PropDoc(value = "访问未授权页面后的重定向路径", defaultValue = "/user/login")
    public static final String PROP_URL_UNAUTH = "shiro.url.unauth";

    @Inject
    protected AppContext appContext;

    @IocBean(name = "shiroEnvironmentLoaderListener")
    public EnvironmentLoaderListener createShiroEnvironmentLoaderListener() {
        NbShiroEnvironmentLoaderListener env = new NbShiroEnvironmentLoaderListener();
        env.appContext = appContext;
        env.conf = conf;
        return env;
    }

    @IocBean(name = "shiroWebEnvironment")
    public WebEnvironment createWebEnvironment() {
        return new DefaultWebEnvironment();
    }

    @IocBean(name = "shiroRememberMeManager")
    public RememberMeManager createRememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setSerializer(new SimplePrincipalSerializer());
        SimpleCookie cookie = new SimpleCookie();
        cookie.setName("rememberMe");
        cookie.setHttpOnly(true);
        rememberMeManager.setCookie(cookie);
        return rememberMeManager;
    }

    @IocBean(name = "shiroWebSecurityManager")
    public WebSecurityManager getWebSecurityManager() {
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager() {
            protected SubjectContext resolveSession(SubjectContext context) {
                if (context.resolveSession() != null) {
                    return context;
                }
                try {
                    Session session = resolveContextSession(context);
                    if (session != null) {
                        context.setSession(session);
                    }
                } catch (InvalidSessionException e) {
                }
                return context;
            }
        };

        // Shiro Session相关
        if (conf.getBoolean(PROP_SESSION_ENABLE, true)) {
            webSecurityManager.setSessionManager(ioc.get(WebSessionManager.class, "shiroWebSessionManager"));
        }
        List<Realm> realms = new ArrayList<>();
        for (String realmName : ioc.getNamesByType(Realm.class)) {
            realms.add(ioc.get(Realm.class, realmName));
        }
        if (ioc.has("authenticationStrategy")){
            ModularRealmAuthenticator modularRealmAuthenticator=new ModularRealmAuthenticator();
            modularRealmAuthenticator.setAuthenticationStrategy(ioc.get(AuthenticationStrategy.class,"authenticationStrategy"));
            if (realms.size() > 0)
                modularRealmAuthenticator.setRealms(realms);
            webSecurityManager.setAuthenticator(modularRealmAuthenticator);
        }else if (realms.size() > 0)
            webSecurityManager.setRealms(realms);
        webSecurityManager.setRememberMeManager(ioc.get(RememberMeManager.class, "shiroRememberMeManager"));
        return webSecurityManager;
    }


    @IocBean(name = "shiroWebSessionManager")
    public WebSessionManager getWebSessionManager() {
        DefaultWebSessionManager webSessionManager = conf.make(DefaultWebSessionManager.class, "shiro.session.manager.");

        // 带缓存的shiro会话
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        sessionDAO.setSessionIdGenerator(new UU32SessionIdGenerator());
        webSessionManager.setSessionDAO(sessionDAO);

        // cookie
        conf.putIfAbsent(PROP_SESSION_COOKIE_NAME, "sid");
        conf.putIfAbsent(PROP_SESSION_COOKIE_MAXAGE, "946080000");
        conf.putIfAbsent(PROP_SESSION_COOKIE_HTTPONLY, "true");

        SimpleCookie cookie = conf.make(SimpleCookie.class, "shiro.session.cookie.");
        webSessionManager.setSessionIdCookie(cookie);
        webSessionManager.setSessionIdCookieEnabled(true);

        webSessionManager.setCacheManager(ioc.get(CacheManager.class, "shiroCacheManager"));

        return webSessionManager;
    }


    @IocBean(name = "shiroCacheManager")
    public CacheManager getCacheManager() {
        switch (conf.get(PROP_SESSION_CACHE_TYPE, "memory")) {
            case "ehcache":
                return ioc.get(CacheManager.class, "shiroEhcacheCacheManager");
            case "redis":
                //return ioc.get(CacheManager.class, "shiroRedisCacheManager");
            case "lcache":
                return ioc.get(CacheManager.class, "shiroLcacheCacheManager");
            case "memory":
                return new MemoryConstrainedCacheManager();
            default:
                throw new ShiroException("unkown shiro.session.cache.type=" + conf.get("shiro.session.cache.type"));
        }

    }

    @IocBean(name = "shiroLcacheCacheManager")
    public CacheManager getShiroLcacheCacheManager(@Inject("refer:shiroEhcacheCacheManager") CacheManager shiroEhcacheCacheManager,
                                                   @Inject("refer:shiroRedisCacheManager") CacheManager shiroRedisCacheManager) {
        LCacheManager cacheManager = new LCacheManager();
        cacheManager.setLevel1(shiroEhcacheCacheManager);
        cacheManager.setLevel2(shiroRedisCacheManager);
        cacheManager.setJedisAgent(ioc.get(JedisAgent.class));
        return cacheManager;
    }

    @IocBean(name = "shiroEhcacheCacheManager")
    public CacheManager getShiroLcacheCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        if (ioc.has("ehcacheCacheManager")) {
            cacheManager.setCacheManager(ioc.get(net.sf.ehcache.CacheManager.class, "ehcacheCacheManager"));
        } else {
            cacheManager.setCacheManager((net.sf.ehcache.CacheManager) _getCacheManager());
        }
        return cacheManager;
    }

    @IocBean(name = "shiroRedisCacheManager")
    public CacheManager getRedisLcacheCacheManager() {
        conf.putIfAbsent(PROP_SESSION_CACHE_REDIS_MODE, "kv");
        conf.putIfAbsent(PROP_SESSION_CACHE_REDIS_DEBUG, "false");
        conf.putIfAbsent(PROP_SESSION_CACHE_REDIS_TTL, "-1");
        RedisCacheManager cacheManager = conf.make(RedisCacheManager.class, "shiro.session.cache.redis.");
        return cacheManager;
    }

    public EventListener getEventListener() {
        return ioc.get(EnvironmentLoaderListener.class, "shiroEnvironmentLoaderListener");
    }

    /**
     * 返回值不能是CacheManager,因为要考虑没有加ehcache的情况
     */
    protected Object _getCacheManager() {
        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.getInstance();
        if (cacheManager != null)
            return cacheManager;
        return net.sf.ehcache.CacheManager.newInstance();
    }
}
