package org.nutz.boot.starter.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.aware.IocAware;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@IocBean
public class JettyStarter implements ClassLoaderAware, IocAware, ServerFace, LifeCycle, AppContextAware {

    private static final Log log = Logs.get();

    protected static final String PRE = "jetty.";

    @PropDoc(value = "监听的ip地址", defaultValue = "0.0.0.0")
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(value = "线程池idleTimeout，单位毫秒", defaultValue = "60000", type = "int")
    public static final String PROP_THREADPOOL_TIMEOUT = PRE + "threadpool.idleTimeout";

    @PropDoc(value = "线程池最小线程数minThreads", defaultValue = "200", type = "int")
    public static final String PROP_THREADPOOL_MINTHREADS = PRE + "threadpool.minThreads";

    @PropDoc(value = "线程池最大线程数maxThreads", defaultValue = "500", type = "int")
    public static final String PROP_THREADPOOL_MAXTHREADS = PRE + "threadpool.maxThreads";

    @PropDoc(value = "监听的端口", defaultValue = "8080", type = "int")
    public static final String PROP_PORT = PRE + "port";

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
    
    //------------------ HttpConfiguration
    @PropDoc(value = "安全协议,例如https")
    public static final String PROP_HTTP_CONFIG_secureScheme = PRE + "httpConfig.secureScheme";
    @PropDoc(value = "安全协议的端口,例如8443")
    public static final String PROP_HTTP_CONFIG_securePort = PRE + "httpConfig.securePort";
    @PropDoc(value = "输出缓冲区大小", defaultValue="32768")
    public static final String PROP_HTTP_CONFIG_outputBufferSize = PRE + "httpConfig.outputBufferSize";
    @PropDoc(value = "输出聚合大小", defaultValue="8192")
    public static final String PROP_HTTP_CONFIG_outputAggregationSize = PRE + "httpConfig.outputAggregationSize";
    @PropDoc(value = "请求的头部最大值", defaultValue="8192")
    public static final String PROP_HTTP_CONFIG_requestHeaderSize = PRE + "httpConfig.requestHeaderSize";
    @PropDoc(value = "响应的头部最大值", defaultValue="8192")
    public static final String PROP_HTTP_CONFIG_responseHeaderSize = PRE + "httpConfig.responseHeaderSize";
    @PropDoc(value = "是否发送jetty版本号", defaultValue="true")
    public static final String PROP_HTTP_CONFIG_sendServerVersion = PRE + "httpConfig.sendServerVersion";
    @PropDoc(value = "是否发送日期信息", defaultValue="true")
    public static final String PROP_HTTP_CONFIG_sendDateHeader = PRE + "httpConfig.sendDateHeader";
    @PropDoc(value = "头部缓冲区大小", defaultValue="8192")
    public static final String PROP_HTTP_CONFIG_headerCacheSize = PRE + "httpConfig.headerCacheSize";
    @PropDoc(value = "最大错误重定向次数", defaultValue="10")
    public static final String PROP_HTTP_CONFIG_maxErrorDispatches = PRE + "httpConfig.maxErrorDispatches";
    @PropDoc(value = "阻塞超时", defaultValue="-1")
    public static final String PROP_HTTP_CONFIG_blockingTimeout = PRE + "httpConfig.blockingTimeout";
    @PropDoc(value = "是否启用持久化连接", defaultValue="true")
    public static final String PROP_HTTP_CONFIG_persistentConnectionsEnabled = PRE + "httpConfig.persistentConnectionsEnabled";
    @PropDoc(value = "自定义404页面")
    public static final String PROP_PAGE_404 = PRE + "page.404";


    @Inject
    private PropertiesProxy conf;

    protected Server server;
    protected ClassLoader classLoader;
    protected Ioc ioc;
    protected AppContext appContext;
    protected WebAppContext wac;

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public boolean failsafe() {
        return false;
    }

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    
    @IocBean(name="jettyServer")
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
        HttpConfiguration httpConfig = conf.make(HttpConfiguration.class, "jetty.httpConfig.");
        HttpConnectionFactory httpFactory = new HttpConnectionFactory( httpConfig );
        ServerConnector connector = new ServerConnector(server, httpFactory);
        connector.setHost(getHost());
        connector.setPort(getPort());
        connector.setIdleTimeout(getIdleTimeout());
        server.setConnectors(new Connector[]{connector});

        // 设置应用上下文
        wac = new WebAppContext();
        wac.setContextPath(getContextPath());
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
        for (String resourcePath : Arrays.asList("static/", "webapp/")) {
            File f = new File(resourcePath);
            if (f.exists()) {
                resources.add(Resource.newResource(f));
            }
            Enumeration<URL> urls = appContext.getClassLoader().getResources(resourcePath);
            while (urls.hasMoreElements()) {
                resources.add(Resource.newResource(urls.nextElement()));
            }
        }
        if (conf.has(PROP_STATIC_PATH_LOCAL)) {
            File f = new File(conf.get(PROP_STATIC_PATH_LOCAL));
            if (f.exists()) {
                log.debug("found static local path, add it : " + f.getAbsolutePath());
                resources.add(0,Resource.newResource(f));
            }
            else {
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
        server.setHandler(wac);
        List<String> list = Configuration.ClassList.serverDefault(server);
        list.add("org.eclipse.jetty.annotations.AnnotationConfiguration");
        wac.setConfigurationClasses(list);
        wac.getServletContext().setExtendedListenerTypes(true);
        wac.getSessionHandler().setMaxInactiveInterval(conf.getInt(PROP_SESSION_TIMEOUT, 30) * 60);

        ErrorPageErrorHandler ep  = new ErrorPageErrorHandler();
        ep.setErrorPages(getErrorPages());
        wac.setErrorHandler(ep);

        // 设置一下额外的东西
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", getMaxFormContentSize());
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
        List<WebFilterFace> filters = appContext.getBeans(WebFilterFace.class);
        Collections.sort(filters, Comparator.comparing(WebFilterFace::getOrder));
        filters.forEach((face) -> addFilter(face));
        appContext.getBeans(WebServletFace.class).forEach((face) -> {
            if (face.getServlet() == null) {
                return;
            }
            ServletHolder holder = new ServletHolder(face.getServlet());
            holder.setName(face.getName());
            holder.setInitParameters(face.getInitParameters());
            wac.addServlet(holder, face.getPathSpec());
        });
        appContext.getBeans(WebEventListenerFace.class).forEach((face) -> {
            if (face.getEventListener() != null) {
                wac.addEventListener(face.getEventListener());
            }
        });
    }

    public Map<String,String> getErrorPages(){
        Map<String,String> pagers = new HashMap<>();
        if(conf.has(PROP_PAGE_404)){
            pagers.put("404",conf.get(PROP_PAGE_404));
        }
        return pagers;
    }
    public void addFilter(WebFilterFace webFilter) {
        if (webFilter == null || webFilter.getFilter() == null) {
            return;
        }
        log.debugf("add filter name=%s pathSpec=%s", webFilter.getName(), webFilter.getPathSpec());
        FilterHolder holder = new FilterHolder(webFilter.getFilter());
        holder.setName(webFilter.getName());
        holder.setInitParameters(webFilter.getInitParameters());
        wac.addFilter(holder, webFilter.getPathSpec(), webFilter.getDispatches());
    }

    public void fetch() throws Exception {}

    public void depose() throws Exception {}

    // --getConf---
    public int getPort() {
        try {
            return appContext.getServerPort(PROP_PORT);
        }
        catch (NoSuchMethodError e) {
            log.info("Please remove 'nutzboot-starter' dependency from pom.xml. https://github.com/nutzam/nutzboot/issues/93");
            return conf.getInt(PROP_PORT, 8080);
        }
    }

    public String getHost() {
        try {
            return appContext.getServerHost(PROP_HOST);
        }
        catch (NoSuchMethodError e) {
            log.info("Please remove 'nutzboot-starter' dependency from pom.xml. https://github.com/nutzam/nutzboot/issues/93");
            return conf.get(PROP_HOST, "0.0.0.0");
        }
    }

    public int getMaxFormContentSize() {
        return conf.getInt(PROP_MAX_FORM_CONTENT_SIZE, 1024 * 1024 * 1024);
    }

    public String getContextPath() {
        return conf.get(PROP_CONTEXT_PATH, "/");
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

}
