package org.nutz.boot.starter.jetty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Deflater;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.MonitorObject;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.servlet3.AbstractServletContainerStarter;
import org.nutz.boot.starter.servlet3.NbServletContextListener;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

@IocBean
public class JettyStarter extends AbstractServletContainerStarter implements ServerFace, MonitorObject {

    private static final Log log = Logs.get();

    protected static final String PRE = "jetty.";

    @PropDoc(value = "监听的端口", defaultValue = "8080", type = "int")
    public static final String PROP_PORT = PRE + "port";

    @PropDoc(value = "监听的ip地址", defaultValue = "0.0.0.0")
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(value = "线程池idleTimeout，单位毫秒", defaultValue = "60000", type = "int")
    public static final String PROP_THREADPOOL_TIMEOUT = PRE + "threadpool.idleTimeout";

    @PropDoc(value = "线程池最小线程数minThreads", defaultValue = "200", type = "int")
    public static final String PROP_THREADPOOL_MINTHREADS = PRE + "threadpool.minThreads";

    @PropDoc(value = "线程池最大线程数maxThreads", defaultValue = "500", type = "int")
    public static final String PROP_THREADPOOL_MAXTHREADS = PRE + "threadpool.maxThreads";

    @PropDoc(value = "空闲时间,单位毫秒", defaultValue = "300000", type = "int")
    public static final String PROP_IDLE_TIMEOUT = PRE + "http.idleTimeout";

    @PropDoc(value = "上下文路径", defaultValue = "/")
    public static final String PROP_CONTEXT_PATH = PRE + "contextPath";

    @PropDoc(value = "表单最大尺寸", defaultValue = "1gb", type = "int")
    public static final String PROP_MAX_FORM_CONTENT_SIZE = PRE + "maxFormContentSize";

    @PropDoc(value = "Session空闲时间,单位分钟", defaultValue = "30", type = "int")
    public static final String PROP_SESSION_TIMEOUT = "web.session.timeout";

    @PropDoc(value = "静态文件所在的本地路径")
    public static final String PROP_STATIC_PATH_LOCAL = PRE + "staticPathLocal";
    @PropDoc(value = "额外的静态文件路径")
    public static final String PROP_STATIC_PATH = PRE + "staticPath";

    // ------------------ HttpConfiguration
    @PropDoc(value = "安全协议,例如https")
    public static final String PROP_HTTP_CONFIG_SECURESCHEME = PRE + "httpConfig.secureScheme";
    @PropDoc(value = "安全协议的端口,例如8443")
    public static final String PROP_HTTP_CONFIG_SECUREPORT = PRE + "httpConfig.securePort";
    @PropDoc(value = "输出缓冲区大小", defaultValue = "32768")
    public static final String PROP_HTTP_CONFIG_OUTPUTBUFFERSIZE = PRE + "httpConfig.outputBufferSize";
    @PropDoc(value = "输出聚合大小", defaultValue = "8192")
    public static final String PROP_HTTP_CONFIG_OUTPUTAGGREGATIONSIZE = PRE + "httpConfig.outputAggregationSize";
    @PropDoc(value = "请求的头部最大值", defaultValue = "8192")
    public static final String PROP_HTTP_CONFIG_REQUESTHEADERSIZE = PRE + "httpConfig.requestHeaderSize";
    @PropDoc(value = "响应的头部最大值", defaultValue = "8192")
    public static final String PROP_HTTP_CONFIG_RESPONSEHEADERSIZE = PRE + "httpConfig.responseHeaderSize";
    @PropDoc(value = "是否发送jetty版本号", defaultValue = "true")
    public static final String PROP_HTTP_CONFIG_SENDSERVERVERSION = PRE + "httpConfig.sendServerVersion";
    @PropDoc(value = "是否发送日期信息", defaultValue = "true")
    public static final String PROP_HTTP_CONFIG_SENDDATEHEADER = PRE + "httpConfig.sendDateHeader";
    @PropDoc(value = "头部缓冲区大小", defaultValue = "8192")
    public static final String PROP_HTTP_CONFIG_HEADERCACHESIZE = PRE + "httpConfig.headerCacheSize";
    @PropDoc(value = "最大错误重定向次数", defaultValue = "10")
    public static final String PROP_HTTP_CONFIG_MAXERRORDISPATCHES = PRE + "httpConfig.maxErrorDispatches";
    @PropDoc(value = "阻塞超时", defaultValue = "-1")
    public static final String PROP_HTTP_CONFIG_BLOCKINGTIMEOUT = PRE + "httpConfig.blockingTimeout";
    @PropDoc(value = "是否启用持久化连接", defaultValue = "true")
    public static final String PROP_HTTP_CONFIG_PERSISTENTCONNECTIONSENABLED = PRE + "httpConfig.persistentConnectionsEnabled";
    @PropDoc(value = "自定义404页面,同理,其他状态码也是支持的")
    public static final String PROP_PAGE_404 = PRE + "page.404";
    @PropDoc(value = "自定义java.lang.Throwable页面,同理,其他异常也支持")
    public static final String PROP_PAGE_THROWABLE = PRE + "page.java.lang.Throwable";
    
    // Gzip
    @PropDoc(value = "是否启用gzip", defaultValue = "false")
    public static final String PROP_GZIP_ENABLE = PRE + "gzip.enable";

    @PropDoc(value = "gzip压缩级别", defaultValue = "-1")
    public static final String PROP_GZIP_LEVEL = PRE + "gzip.level";

    @PropDoc(value = "gzip压缩最小触发大小", defaultValue = "512")
    public static final String PROP_GZIP_MIN_CONTENT_SIZE = PRE + "gzip.minContentSize";

    @PropDoc(value = "WelcomeFile列表", defaultValue="index.html,index.htm,index.do")
    public static final String PROP_WELCOME_FILES = PRE + "welcome_files";
    
    // HTTPS相关
    @PropDoc(value = "Https端口号")
    public static final String PROP_HTTPS_PORT = PRE + "https.port";
    @PropDoc(value = "Https的KeyStore路径")
    public static final String PROP_HTTPS_KEYSTORE_PATH = PRE + "https.keystore.path";
    @PropDoc(value = "Https的KeyStore的密码")
    public static final String PROP_HTTPS_KEYSTORE_PASSWORD = PRE + "https.keystore.password";

    protected Server server;
    protected WebAppContext wac;
    protected ServerConnector connector;

    public void start() throws Exception {
        server.start();
        if (log.isDebugEnabled())
            log.debug("Jetty monitor props:\r\n"+getMonitorForPrint());
    }

    public void stop() throws Exception {
        server.stop();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    @IocBean(name = "jettyServer")
    public Server getJettyServer() {
        return server;
    }

    public void init() throws Exception {

        // 创建基础服务器
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setIdleTimeout(getThreadPoolIdleTimeout());
        threadPool.setMinThreads(getMinThreads());
        threadPool.setMaxThreads(getMaxThreads());
        server = new Server(threadPool);
        // HTTP端口设置
        HttpConfiguration httpConfig = conf.make(HttpConfiguration.class, "jetty.httpConfig.");
        HttpConnectionFactory httpFactory = new HttpConnectionFactory(httpConfig);
        connector = new ServerConnector(server, httpFactory);
        connector.setHost(getHost());
        connector.setPort(getPort());
        connector.setIdleTimeout(getIdleTimeout());
        server.addConnector(connector);

        updateMonitorValue("http.port", connector.getPort());
        updateMonitorValue("http.host", connector.getHost());
        updateMonitorValue("http.idleTimeout", connector.getIdleTimeout());

        // 看看Https设置
        int httpsPort = conf.getInt(PROP_HTTPS_PORT);
        if (httpsPort > 0) {
            log.info("found https port " + httpsPort);
            HttpConfiguration https_config = conf.make(HttpConfiguration.class, "jetty.httpsConfig.");;
            https_config.setSecureScheme("https");

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(conf.get(PROP_HTTPS_KEYSTORE_PATH));
            // 私钥
            sslContextFactory.setKeyStorePassword(conf.get(PROP_HTTPS_KEYSTORE_PASSWORD));
            // 公钥
            sslContextFactory.setKeyManagerPassword(conf.get("jetty.https.keymanager.password"));

            ServerConnector httpsConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory,"http/1.1"),
                    new HttpConnectionFactory(https_config));
                    // 设置访问端口
            httpsConnector.setPort(httpsPort);
            httpsConnector.setHost(getHost());
            httpsConnector.setIdleTimeout(getIdleTimeout());
            server.addConnector(httpsConnector);
            
            updateMonitorValue("https.enable", true);
            updateMonitorValue("https.port", httpsConnector.getPort());
            updateMonitorValue("https.host", httpsConnector.getHost());
            updateMonitorValue("https.idleTimeout", httpsConnector.getIdleTimeout());
        }
        else {
            updateMonitorValue("https.enable", false);
        }
        
        

        // 设置应用上下文
        wac = new WebAppContext();
        wac.setContextPath(getContextPath());
        
        //wac.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
        //wac.setAttribute("WebAppContext", value);
        // wac.setExtractWAR(false);
        // wac.setCopyWebInf(true);
        // wac.setProtectedTargets(new String[]{"/java", "/javax", "/org",
        // "/net", "/WEB-INF", "/META-INF"});
        wac.setTempDirectory(new File("temp"));
        wac.setClassLoader(classLoader);
        wac.setConfigurationDiscovered(true);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            wac.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        }

        List<Resource> resources = new ArrayList<>();
        for (String resourcePath : getResourcePaths()) {
            File f = new File(resourcePath);
            if (f.exists()) {
                resources.add(Resource.newResource(f));
            }
            Enumeration<URL> urls = appContext.getClassLoader().getResources(resourcePath);
            while (urls.hasMoreElements()) {
                resources.add(Resource.newResource(urls.nextElement()));
            }
        }
        if (resources.isEmpty()) {
            resources.add(Resource.newClassPathResource("META-INF/jetty_resources"));
        }
        if (conf.has(PROP_STATIC_PATH_LOCAL)) {
            File f = new File(conf.get(PROP_STATIC_PATH_LOCAL));
            if (f.exists()) {
                log.debug("found static local path, add it : " + f.getAbsolutePath());
                resources.add(0, Resource.newResource(f));
            } else {
                log.debug("static local path not exist, skip it : " + f.getPath());
            }
        }
        wac.setBaseResource(new ResourceCollection(resources.toArray(new Resource[resources.size()])) {
            @Override
            public Resource addPath(String path) throws IOException, MalformedURLException {
                // TODO 为啥ResourceCollection读取WEB-INF的时候返回null
                // 从而导致org.eclipse.jetty.webapp.WebAppContext.getWebInf()抛NPE
                // 先临时hack吧
                Resource resource = super.addPath(path);
                if (resource == null && "WEB-INF/".equals(path)) {
                    return Resource.newResource(new File("XXXX"));
                }
                return resource;
            }
        });
        if (conf.getBoolean(PROP_GZIP_ENABLE, false)) {
            GzipHandler gzip = new GzipHandler();
            gzip.setHandler(wac);
            gzip.setMinGzipSize(conf.getInt(PROP_GZIP_MIN_CONTENT_SIZE, 512));
            gzip.setCompressionLevel(conf.getInt(PROP_GZIP_LEVEL, Deflater.DEFAULT_COMPRESSION));
            server.setHandler(gzip);
        }
        else {
            server.setHandler(wac);
        }
        List<String> list = Configuration.ClassList.serverDefault(server);
        list.add("org.eclipse.jetty.annotations.AnnotationConfiguration");
        list.add("org.eclipse.jetty.webapp.MetaInfConfiguration");
        wac.setConfigurationClasses(list);
        wac.getServletContext().setExtendedListenerTypes(true);
        wac.getSessionHandler().setMaxInactiveInterval(getSessionTimeout());

        ErrorHandler ep = Lang.first(appContext.getBeans(ErrorHandler.class));
        if(ep == null){
            ErrorPageErrorHandler handler = new ErrorPageErrorHandler();
            handler.setErrorPages(getErrorPages());
            ep = handler;
        }
        wac.setErrorHandler(ep);
        wac.setWelcomeFiles(getWelcomeFiles());
        wac.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        updateMonitorValue("welcome_files", Strings.join(",", wac.getWelcomeFiles()));

        // 设置一下额外的东西
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", getMaxFormContentSize());
        updateMonitorValue("maxFormContentSize", server.getAttribute("org.eclipse.jetty.server.Request.maxFormContentSize"));
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        addNutzSupport();

        ServerContainer sc = WebSocketServerContainerInitializer.configureContext(wac);
        for (Class<?> klass : Scans.me().scanPackage(appContext.getPackage())) {
            if (klass.getAnnotation(ServerEndpoint.class) != null) {
                sc.addEndpoint(klass);
            }
        }
    }

    private void addNutzSupport() {
        wac.addEventListener(ioc.get(NbServletContextListener.class));
    }

    public int getMaxFormContentSize() {
        return conf.getInt(PROP_MAX_FORM_CONTENT_SIZE, 1024 * 1024 * 1024);
    }

    public int getIdleTimeout() {
        return conf.getInt(PROP_IDLE_TIMEOUT, 300 * 1000);
    }

    public int getMinThreads() {
        return Lang.isAndroid ? 8 : conf.getInt(PROP_THREADPOOL_MINTHREADS, 200);
    }

    public int getMaxThreads() {
        return Lang.isAndroid ? 50 : conf.getInt(PROP_THREADPOOL_MAXTHREADS, 500);
    }

    public int getThreadPoolIdleTimeout() {
        return conf.getInt(PROP_THREADPOOL_TIMEOUT, 60 * 1000);
    }

    protected String getConfigurePrefix() {
        return PRE;
    }

    public String getMonitorName() {
        return "jetty";
    }
}
