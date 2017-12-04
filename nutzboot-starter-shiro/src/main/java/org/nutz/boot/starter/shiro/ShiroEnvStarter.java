package org.nutz.boot.starter.shiro;

import java.util.EventListener;

import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.integration.jedis.JedisAgent;
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

	@IocBean(name = "shiroWebSecurityManager")
	public WebSecurityManager getWebSecurityManager() {
		DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();

		// Shiro Session相关
		if (conf.getBoolean("shiro.session.enable", true)) {
			webSecurityManager.setSessionManager(ioc.get(WebSessionManager.class, "shiroWebSessionManager"));
		}
		if (ioc.has(conf.get("shiro.realm.names", "shiroRealm"))) {
			webSecurityManager.setRealm(ioc.get(Realm.class, conf.get("shiro.realm.names", "shiroRealm")));
		}
		return webSecurityManager;
	}

	@IocBean(name = "shiroFilterChainResolver")
	public FilterChainResolver getFilterChainResolver() {
		PathMatchingFilterChainResolver filterChainResolver = new PathMatchingFilterChainResolver();

		return filterChainResolver;
	}

	@IocBean(name = "shiroWebSessionManager")
	public WebSessionManager getWebSessionManager() {
		DefaultWebSessionManager webSessionManager = conf.make(DefaultWebSessionManager.class, "shiro.session.manager.");

		// 带缓存的shiro会话
		EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
		sessionDAO.setSessionIdGenerator(new UU32SessionIdGenerator());
		webSessionManager.setSessionDAO(sessionDAO);

		// cookie
		conf.putIfAbsent("shiro.session.cookie.name", "sid");
		conf.putIfAbsent("shiro.session.cookie.maxAge", "946080000");
		conf.putIfAbsent("shiro.session.cookie.httpOnly", "true");
		SimpleCookie cookie = conf.make(SimpleCookie.class, "shiro.session.cookie.");
		webSessionManager.setSessionIdCookie(cookie);
		webSessionManager.setSessionIdCookieEnabled(true);
		
		webSessionManager.setCacheManager(ioc.get(CacheManager.class, "shiroCacheManager"));

		return webSessionManager;
	}
	
	@IocBean(name="shiroCacheManager")
	public CacheManager getCacheManager() {
		switch (conf.get("shiro.session.cache.type", "memory")) {
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
	
	@IocBean(name="shiroLcacheCacheManager")
	public CacheManager getShiroLcacheCacheManager(@Inject("refer:shiroEhcacheCacheManager")CacheManager shiroEhcacheCacheManager,
			@Inject("refer:shiroRedisCacheManager")CacheManager shiroRedisCacheManager) {
		LCacheManager cacheManager = new LCacheManager();
		cacheManager.setLevel1(shiroEhcacheCacheManager);
		cacheManager.setLevel2(shiroRedisCacheManager);
		cacheManager.setJedisAgent(ioc.get(JedisAgent.class));
		return cacheManager;
	}

	@IocBean(name="shiroEhcacheCacheManager")
	public CacheManager getShiroLcacheCacheManager() {
		EhCacheManager cacheManager = new EhCacheManager();
		if (ioc.has("ehcacheCacheManager")) {
			cacheManager.setCacheManager(ioc.get(net.sf.ehcache.CacheManager.class, "ehcacheCacheManager"));
		}
		else {
			cacheManager.setCacheManagerConfigFile(conf.get("shiro.session.cache.ehcache.cacheManagerConfigFile", "classpath:ehcache.xml"));
		}
		return cacheManager;
	}
	
	@IocBean(name="shiroRedisCacheManager")
	public CacheManager getRedisLcacheCacheManager() {
		conf.putIfAbsent("shiro.session.cache.redis.mode", "kv");
		conf.putIfAbsent("shiro.session.cache.redis.debug", "false");
		conf.putIfAbsent("shiro.session.cache.redis.ttl", "-1");
		RedisCacheManager cacheManager = conf.make(RedisCacheManager.class, "shiro.session.cache.redis.");
		return cacheManager;
	}

	public EventListener getEventListener() {
		return ioc.get(EnvironmentLoaderListener.class, "shiroEnvironmentLoaderListener");
	}
}
