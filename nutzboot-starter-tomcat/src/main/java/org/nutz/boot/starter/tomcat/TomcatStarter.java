package org.nutz.boot.starter.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletContext;


/**
 * tomcat 启动器
 *
 * @author benjobs (benjobs@qq.com)
 * @author wendal (wendal1985@gmail.com)
 */
@IocBean
public class TomcatStarter implements ClassLoaderAware, ServerFace, LifeCycle, AppContextAware {

    private static final Log log = Logs.get();

    protected static final String PRE = "tomcat.";

    @PropDoc(group = "tomcat", value = "监听的ip地址", defaultValue = "0.0.0.0")
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(group = "tomcat", value = "监听的端口", defaultValue = "8080", type = "int")
    public static final String PROP_PORT = PRE + "port";

    @PropDoc(group = "tomcat", value = "上下文路径")
    public static final String PROP_CONTEXT_PATH = PRE + "contextPath";

    @PropDoc(group = "tomcat", value = "session过期时间", defaultValue = "20")
    public static final String PROP_SESSION = PRE + "session";

    @PropDoc(group = "tomcat", value = "过滤器顺序", defaultValue = "whale,druid,shiro,nutz")
    public static final String PROP_WEB_FILTERS_ORDER = "web.filters.order";

    @PropDoc(group = "tomcat", value = "静态文件路径", defaultValue = "/static/")
    public static final String PROP_STATIC_PATH = PRE + "staticPath";

    public static final String PROP_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    protected Tomcat tomcat;
    protected StandardContext tomcatContext;
    protected ClassLoader classLoader;
    protected AppContext appContext;
    @Inject
    private PropertiesProxy conf;

    private static final AtomicInteger containerCounter = new AtomicInteger(-1);

    private final Object monitor = new Object();

    private volatile boolean started;

    private Thread tomcatThread;

    @Override
    public void init() throws LifecycleException {

        this.tomcat = new Tomcat();

        File baseDir = createTempDir("tomcat");
        this.tomcat.setBaseDir(baseDir.getAbsolutePath());

        Connector connector = new Connector(PROP_PROTOCOL);
        connector.setPort(getPort());
        connector.setURIEncoding("UTF-8");

        // 设置一下最大线程数
        this.tomcat.getService().addConnector(connector);
        StandardThreadExecutor executor = new StandardThreadExecutor();
        executor.setMaxThreads(getMaxThread());
        connector.getService().addExecutor(executor);

        this.tomcat.setConnector(connector);

        this.tomcat.setHostname(getHost());
        this.tomcat.getHost().setAutoDeploy(false);
        this.tomcat.getEngine().setBackgroundProcessorDelay(30);

        this.prepareContext();
    }

    public void start() throws LifecycleException {
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            this.tomcat.start();

            tomcatThread = new Thread("container-" + (containerCounter.get())) {
                @Override
                public void run() {
                    TomcatStarter.this.tomcat.getServer().await();
                }
            };
            tomcatThread.setContextClassLoader(getClass().getClassLoader());
            tomcatThread.setDaemon(false);
            tomcatThread.start();
            this.started = true;
        }
    }

    public void stop() throws LifecycleException {
        synchronized (this.monitor) {
            if (started) {
                this.tomcat.stop();
                this.tomcat = null;
                this.tomcatThread.interrupt();
                this.started = false;
            }
        }
    }

    public boolean isRunning() {
        return this.started;
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean failsafe() {
        return false;
    }

    public void fetch() {}

    public void depose() throws LifecycleException {
        if (this.tomcat != null) {
            this.stop();
        }
    }

    private void prepareContext() {

        File docBase = createTempDir("tomcat-docbase");

        this.tomcatContext = new StandardContext();
        this.tomcatContext.setDocBase(docBase.getAbsolutePath());
        this.tomcatContext.setName(getContextPath());
        this.tomcatContext.setPath(getContextPath());
        this.tomcatContext.addLifecycleListener(new Tomcat.FixContextListener());
        this.tomcatContext.setParentClassLoader(classLoader);
        this.tomcatContext.setSessionTimeout(getSessionTimeout());
        this.tomcatContext.addLifecycleListener(new StoreMergedWebXmlListener());

        try {
            this.tomcatContext.setUseRelativeRedirects(false);
        } catch (NoSuchMethodError ex) {
            // Tomcat is < 8.0.30. Continue
        }

        for (String welcomeFile : Arrays.asList("index.html",
                "index.htm",
                "index.jsp",
                "index.do")) {
            this.tomcatContext.addWelcomeFile(welcomeFile);
        }

        // 注册defaultServlet
        addDefaultServlet();

        addNutzSupport();

        this.tomcat.getHost().addChild(tomcatContext);
    }

    private void addNutzSupport() {
        Map<String, WebFilterFace> filters = new HashMap<String, WebFilterFace>();
        for (Object object : appContext.getStarters()) {
            if (object instanceof WebFilterFace) {
                WebFilterFace webFilter = (WebFilterFace) object;
                if (webFilter == null || webFilter.getFilter() == null) {
                    continue;
                }
                filters.put(webFilter.getName(), webFilter);
            }
            if (object instanceof WebServletFace) {
                WebServletFace webServlet = (WebServletFace) object;
                if (webServlet == null || webServlet.getServlet() == null) {
                    continue;
                }
                addServlet(webServlet);
            }
            if (object instanceof WebEventListenerFace) {
                WebEventListenerFace contextListener = (WebEventListenerFace) object;
                if (contextListener == null || contextListener.getEventListener() == null) {
                    continue;
                }
                this.tomcatContext.addApplicationEventListener(contextListener.getEventListener());
            }
        }

        String _filterOrders = conf.get(PROP_WEB_FILTERS_ORDER);
        if (_filterOrders == null)
            _filterOrders = "whale,druid,shiro,nutz";
        else if (_filterOrders.endsWith("+")) {
            _filterOrders = _filterOrders.substring(0, _filterOrders.length() - 1)
                    + ",whale,druid,shiro,nutz";
        }
        String[] filterOrders = Strings.splitIgnoreBlank(_filterOrders);

        for (String filterName : filterOrders) {
            addFilter(filters.remove(filterName));
        }
        for (WebFilterFace webFilter : filters.values()) {
            addFilter(webFilter);
        }
    }

    private void addDefaultServlet() {
        Wrapper defaultServlet = this.tomcatContext.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);
        defaultServlet.setOverridable(true);
        this.tomcatContext.addChild(defaultServlet);
        addServletMapping("/", "default");
    }

    private void addServlet(WebServletFace webServlet) {
        log.debugf("[NutzBoot] add servlet name=%s pathSpec=%s",
                webServlet.getName(),
                webServlet.getPathSpec());

        Wrapper servlet = tomcatContext.createWrapper();
        servlet.setName(webServlet.getName());
        servlet.setServletClass(webServlet.getServlet().getClass().getName());

        for (Map.Entry<String, String> entry : webServlet.getInitParameters().entrySet()) {
            servlet.addInitParameter(entry.getKey(), entry.getValue());
        }
        servlet.setOverridable(true);
        this.tomcatContext.addChild(servlet);
        addServletMapping(webServlet.getPathSpec(), webServlet.getName());
    }

    private void addFilter(WebFilterFace filterFace) {
        if (filterFace == null || filterFace.getFilter() == null) {
            return;
        }
        log.debugf("[NutzBoot] add filter name=%s pathSpec=%s",filterFace.getName(),filterFace.getPathSpec());
        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(filterFace.getFilter());
        filterDef.setFilterName(filterFace.getName());
        filterDef.setFilterClass(filterFace.getFilter().getClass().getName());
        if (filterFace.getInitParameters() != null && !filterFace.getInitParameters().isEmpty()) {
            for (Map.Entry<String, String> entry : filterFace.getInitParameters().entrySet()) {
                filterDef.addInitParameter(entry.getKey(), entry.getValue());
            }
        }
        this.tomcatContext.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.addURLPatternDecoded(filterFace.getPathSpec());
        filterMap.setFilterName(filterFace.getName());
        this.tomcatContext.addFilterMap(filterMap);
    }

    private void addServletMapping(String pattern, String name) {
        this.tomcatContext.addServletMappingDecoded(pattern, name);
    }

    private File createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + getPort());
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Unable to create tempDir. java.io.tmpdir is set to "
                            + System.getProperty("java.io.tmpdir"),
                    ex);
        }
    }

    private static class StoreMergedWebXmlListener implements LifecycleListener {

        private static final String MERGED_WEB_XML = "org.apache.tomcat.util.scan.MergedWebXml";

        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                onStart((Context) event.getLifecycle());
            }
        }

        private void onStart(Context context) {
            ServletContext servletContext = context.getServletContext();
            if (servletContext.getAttribute(MERGED_WEB_XML) == null) {
                servletContext.setAttribute(MERGED_WEB_XML, getEmptyWebXml());
            }
        }

        private String getEmptyWebXml() {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("empty-web.xml");
            if (stream == null) {
                throw new IllegalArgumentException("Unable to read empty web.xml");
            }
            try {
                try {
                    StringBuilder out = new StringBuilder();
                    InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
                    char[] buffer = new char[ 1024*4 ];
                    int bytesRead = -1;
                    while ((bytesRead = reader.read(buffer)) != -1) {
                        out.append(buffer, 0, bytesRead);
                    }
                    return out.toString();
                } finally {
                    stream.close();
                }
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

    }


    // --getConf---
    public int getPort() {
        return conf.getInt(PROP_PORT, 8080);
    }

    public String getHost() {
        return conf.get(PROP_HOST, "0.0.0.0");
    }

    public String getStaticPath() {
        return conf.get(PROP_STATIC_PATH, "static");
    }

    public String getContextPath() {
        return conf.get(PROP_CONTEXT_PATH, "");
    }

    public int getSessionTimeout() {
        return conf.getInt(PROP_SESSION, 30);
    }

    public int getMaxThread() {
        return Lang.isAndroid ? 50 : 500;
    }
}
