package org.nutz.boot.starter.tio.mvc;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.session.id.ISessionIdGenerator;
import org.tio.http.server.HttpServerStarter;
import org.tio.http.server.handler.DefaultHttpRequestHandler;
import org.tio.http.server.intf.HttpServerInterceptor;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.mvc.intf.ControllerFactory;
import org.tio.utils.cache.ICache;

@IocBean
public class TioMvcHttpServerBeans implements ControllerFactory {
    
    private static final Log log = Logs.get();

    @Inject
    private AppContext appContext;
    
    protected static final String PRE = "tio_mvc.";

    @Inject
    protected PropertiesProxy conf;
    @PropDoc(value = "tio监听端口", defaultValue = "9900")
    public static final String PROP_PORT = PRE + "port";
    @PropDoc(value = "tio监听ip/主机名", defaultValue = "0.0.0.0")
    public static final String PROP_IP = PRE + "host";
    @PropDoc(value = "tio mvc会话超时时间", defaultValue="1800")
    public static final String PROP_SESSION_TIMEOUT = PRE + "sessionTimeout";
    @PropDoc(value = "tio mvc上下文路径")
    public static final String PROP_CONTEXT_PATH = PRE + "contextPath";
    @PropDoc(value = "服务器信息")
    public static final String PROP_SERVER_INFO = PRE + "serverInfo";
    @PropDoc(value = "默认后缀")
    public static final String PROP_SUFFIX = PRE + "suffix";
    @PropDoc(value = "字符集", defaultValue="UTF-8")
    public static final String PROP_CHARSET = PRE + "charset";
    @PropDoc(value = "默认Welcome File", defaultValue="index.html")
    public static final String PROP_WELCOME_FILE = PRE + "welcomeFile";
    @PropDoc(value = "允许访问的域名,逗号分隔")
    public static final String PROP_ALLOW_DOMAINS = PRE + "allowDomains";
    @PropDoc(value = "会话缓存的名称")
    public static final String PROP_SESSION_CACHE_NAME = PRE + "sessionCacheName";
    @PropDoc(value = "会话cookie的名字")
    public static final String PROP_SESSION_COOKIE_NAME = PRE + "sessionCookieName";
    @PropDoc(value = "maxLiveTimeOfStaticRes设置")
    public static final String PROP_MAX_LIVE_TIME_OF_STATIC_RES = PRE + "maxLiveTimeOfStaticRes";
    @PropDoc(value = "404页面", defaultValue="/404.html")
    public static final String PROP_PAGE_404 = PRE + "page404";
    @PropDoc(value = "500页面", defaultValue="/500.html")
    public static final String PROP_PAGE_500 = PRE + "page500";
    @PropDoc(value = "是否使用Session机制", defaultValue="true")
    public static final String PROP_USE_SESSION = PRE + "useSession";
    @PropDoc(value = "拦截器", defaultValue="apiInterceptor")
    public static final String PROP_API_INTERCEPTOR = PRE + "apiInterceptor";
    @PropDoc(value = "会话id生成器", defaultValue="sessionIdGenerator")
    public static final String PROP_SESSION_ID_GENERATOR = PRE + "sessionIdGenerator";
    @PropDoc(value = "会话id缓存提供者", defaultValue="sessionStore")
    public static final String PROP_SESSION_STORE = PRE + "sessionStore";
    
    @IocBean
    public HttpConfig getHttpConfig() {
        String ip = appContext.getServerHost(PROP_IP);
        int port = appContext.getServerPort(PROP_PORT, 9900);
        HttpConfig httpConfig = new HttpConfig(port, 
                                               conf.getLong(PROP_SESSION_TIMEOUT, 1800), 
                                               conf.get(PROP_CONTEXT_PATH),
                                               conf.get(PROP_SUFFIX));
        httpConfig.setBindIp(ip);
        if (conf.has(PROP_ALLOW_DOMAINS)) {
            httpConfig.setAllowDomains(Strings.splitIgnoreBlank(conf.get(PROP_ALLOW_DOMAINS)));
        }
        if (conf.has(PROP_SERVER_INFO)) {
            httpConfig.setServerInfo(conf.get(PROP_SERVER_INFO));
        }
        httpConfig.setCharset(conf.get(PROP_CHARSET, "UTF-8"));
        httpConfig.setWelcomeFile(conf.get(PROP_WELCOME_FILE, "index.html"));
        if (conf.has(PROP_SESSION_CACHE_NAME)) {
            httpConfig.setSessionCacheName(PROP_SESSION_CACHE_NAME);
        }
        if (conf.has(PROP_SESSION_COOKIE_NAME)) {
            httpConfig.setSessionCookieName(conf.get(PROP_SESSION_COOKIE_NAME));
        }
        if (conf.has(PROP_MAX_LIVE_TIME_OF_STATIC_RES)) {
            httpConfig.setMaxLiveTimeOfStaticRes(conf.getInt(PROP_MAX_LIVE_TIME_OF_STATIC_RES));
        }
        httpConfig.setPage404(conf.get(PROP_PAGE_404, "/404.html"));
        httpConfig.setPage500(conf.get(PROP_PAGE_500, "/500.html"));
        httpConfig.setUseSession(conf.getBoolean(PROP_USE_SESSION, true));
        if (httpConfig.isUseSession()) {
            String sessionIdGeneratorName = conf.get(PROP_SESSION_ID_GENERATOR, "sessionIdGenerator");
            if (appContext.getIoc().has(sessionIdGeneratorName)) {
                httpConfig.setSessionIdGenerator(appContext.getIoc().get(ISessionIdGenerator.class, sessionIdGeneratorName));
            }
            else {
                log.debugf("sessionIdGenerator name=%s not found in ioc , skiped", sessionIdGeneratorName);
            }
            String sessionStoreName = conf.get(PROP_SESSION_STORE, "sessionStore");
            if (appContext.getIoc().has(sessionStoreName)) {
                httpConfig.setSessionStore(appContext.getIoc().get(ICache.class, sessionStoreName));
            }
            else {
                log.debugf("sessionStore name=%s not found in ioc , skiped", sessionStoreName);
            }
        }
        return httpConfig;
    }

    @IocBean
    public HttpServerStarter getHttpServerStarter(HttpConfig httpConfig) {
        String[] scanPackages = new String[]{appContext.getPackage()};//tio mvc需要扫描的根目录包
        Routes routes = new Routes(scanPackages, this);
        DefaultHttpRequestHandler requestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
        String apiInterceptorName = conf.get(PROP_API_INTERCEPTOR, "apiInterceptor");
        if (appContext.getIoc().has(apiInterceptorName)) {
            requestHandler.setHttpServerInterceptor(appContext.ioc().get(HttpServerInterceptor.class, apiInterceptorName));
        }
        else {
            log.debugf("apiInterceptor name=%s not found in ioc , skiped", apiInterceptorName);
        }
        return new HttpServerStarter(httpConfig, requestHandler);
    }

    public Object getInstance(Class<?> controllerClazz) throws Exception {
        if (controllerClazz.getAnnotation(IocBean.class) == null)
            return controllerClazz.newInstance();
        return appContext.getIoc().get(controllerClazz);
    }
}
