package org.nutz.boot.starter.jetty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
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

    public void init() throws Exception {

        // 创建基础服务器
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setIdleTimeout(getThreadPoolIdleTimeout());
        threadPool.setMinThreads(getMinThreads());
        threadPool.setMaxThreads(getMaxThreads());
        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server);
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
        wac.setTempDirectory(createTempDir("jetty").getAbsoluteFile());
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

        // 设置一下额外的东西
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", getMaxFormContentSize());
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        ServerContainer sc = WebSocketServerContainerInitializer.configureContext(wac);
        for (Class<?> klass : Scans.me().scanPackage(appContext.getPackage())) {
            if (klass.getAnnotation(ServerEndpoint.class) != null) {
                sc.addEndpoint(klass);
            }
        }

        addNutzSupport();
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

    private File createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + getPort());
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir;
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
        }
    }

    // --getConf---
    public int getPort() {
        return conf.getInt(PROP_PORT, 8080);
    }

    public String getHost() {
        return conf.get(PROP_HOST, "0.0.0.0");
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
