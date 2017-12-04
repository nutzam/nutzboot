package org.nutz.boot.starter.tomcat;


import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
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
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tomcat 启动器
 *
 * @author benjobs (benjobs@qq.com)
 */
public class TomcatStarter implements ClassLoaderAware, IocAware, ServerFace, LifeCycle, AppContextAware {

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


    protected Tomcat tomcat;
    protected ClassLoader classLoader;
    protected Ioc ioc;
    protected AppContext appContext;

    //private Context tomcatContext;
    private Context tomcatContext;

    private volatile boolean started;
    private PropertiesProxy conf;

    private final Object monitor = new Object();

    private static final AtomicInteger containerCounter = new AtomicInteger(-1);

    public int getPort() {
        return conf.getInt(PROP_PORT, 8080);
    }

    public String getHost() {
        return conf.get(PROP_HOST, "0.0.0.0");
    }

    public String getContextPath() {
        return conf.get(PROP_CONTEXT_PATH, "");

    }

    public String getStaticPath() {
        return conf.get(PROP_STATIC_PATH, "static");
    }

    public int getSessionTimeout() {
        return conf.getInt(PROP_SESSION, 30);
    }

    public int getMaxThread() {
        return Lang.isAndroid ? 50 : 500;
    }

    @Override
    public void init() throws Exception {

        synchronized (this.monitor) {

            this.tomcat = new Tomcat();

            conf = appContext.getConfigureLoader().get();

            //init param
            tomcat.setPort(getPort());
            tomcat.setHostname(getHost());


            File baseDir = createTempDir("tomcat");

            tomcat.setBaseDir(baseDir.getAbsolutePath());

            tomcat.getHost().setAutoDeploy(false);

            addInstanceIdToEngineName();

            //maxThread
            StandardThreadExecutor executor = new StandardThreadExecutor();
            executor.setMaxThreads(getMaxThread());
            tomcat.getConnector().getService().addExecutor(executor);

            //add LifecycleListener
            tomcat.getServer().addLifecycleListener(new LifecycleListener() {
                public void lifecycleEvent(LifecycleEvent le) {
                    String lc = le.getType();
                    if (lc.equals(Lifecycle.START_EVENT)) {
                        log.info("[NutzBoot]tomcatServer Starting....");
                    } else if (lc.equals(Lifecycle.STOP_EVENT)) {
                        log.info("[NutzBoot]tomcatServer Stopping....");
                    }
                }
            });

            tomcat.getServer().setParentClassLoader(this.appContext.getClassLoader());

            URL staticURL = appContext.getClassLoader().getResource(getStaticPath());

            File staticFile = new File(staticURL.getPath());
            if (staticFile.exists()) {
                tomcatContext = tomcat.addWebapp(getContextPath(), staticFile.getAbsolutePath());
            } else {
                throw new IllegalArgumentException("[NutzBoot] please check the static file " + staticFile.getAbsolutePath() + " exists!");
            }

            //sessionTimeOut
            tomcatContext.setSessionTimeout(getSessionTimeout());

            for (String welcomeFile : Arrays.asList("index.html", "index.htm", "index.jsp","index.do")) {
                tomcatContext.addWelcomeFile(welcomeFile);
            }

            // 添加其他starter提供的WebXXXX服务
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
                    addListener(contextListener);
                }
            }
            String _filterOrders = conf.get(PROP_WEB_FILTERS_ORDER);
            if (_filterOrders == null)
                _filterOrders = "whale,druid,shiro,nutz";
            else if (_filterOrders.endsWith("+")) {
                _filterOrders = _filterOrders.substring(0, _filterOrders.length() - 1) + ",whale,druid,shiro,nutz";
            }
            String[] filterOrders = Strings.splitIgnoreBlank(_filterOrders);

            for (String filterName : filterOrders) {
                addFilter(filters.remove(filterName));
            }
            for (WebFilterFace webFilter : filters.values()) {
                addFilter(webFilter);
            }

        }
    }

    @Override
    public void start() {
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            try {
                this.tomcat.start();
                this.tomcatContext.setName("NutzBoot-"+containerCounter.get());
                startDaemonAwaitThread();
                this.started = true;
                log.info("Tomcat started on port(s): " + getPortsDescription(true));
            } catch (java.lang.Exception ex) {
                stopSilently();
            }
        }
    }

    @Override
    public void stop() throws LifecycleException {
        this.tomcat.stop();
        this.started = false;
    }

    @Override
    public boolean isRunning() {
        return this.started;
    }

    protected File createTempDir(String prefix) {
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

    private void addServlet(WebServletFace webServlet) {
        log.debugf("[NutzBoot] add servlet name=%s pathSpec=%s", webServlet.getName(), webServlet.getPathSpec());
        Wrapper wrapper = tomcat.addServlet(webServlet.getPathSpec(), webServlet.getName(), webServlet.getServlet());
        if (webServlet.getInitParameters() != null && !webServlet.getInitParameters().isEmpty()) {
            for (Map.Entry<String, String> entry : webServlet.getInitParameters().entrySet()) {
                wrapper.addInitParameter(entry.getKey(), entry.getValue());
            }
        }
        wrapper.addMapping(webServlet.getPathSpec());
    }

    private void addFilter(WebFilterFace filterFace) {
        if (filterFace == null || filterFace.getFilter() == null) {
            return;
        }

        log.debugf("[NutzBoot] add filter name=%s pathSpec=%s", filterFace.getName(), filterFace.getPathSpec());
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
    }

    private void addListener(WebEventListenerFace contextListener) {
        tomcatContext.getServletContext().addListener(contextListener.getEventListener());
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    @Override
    public boolean failsafe() {
        return false;
    }


    @Override
    public void fetch() {

    }

    @Override
    public void depose() {

    }

    private void addInstanceIdToEngineName() {
        int instanceId = containerCounter.incrementAndGet();
        if (instanceId > 0) {
            Engine engine = this.tomcat.getEngine();
            engine.setName(engine.getName() + "-" + instanceId);
        }
    }

    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread("container-" + (containerCounter.get())) {
            @Override
            public void run() {
                TomcatStarter.this.tomcat.getServer().await();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    private void stopSilently() {
        try {
            stop();
        } catch (LifecycleException ex) {
            // Ignore
        }
    }

    private String getPortsDescription(boolean localPort) {
        StringBuilder ports = new StringBuilder();
        for (Connector connector : this.tomcat.getService().findConnectors()) {
            ports.append(ports.length() == 0 ? "" : " ");
            int port = (localPort ? connector.getLocalPort() : connector.getPort());
            ports.append(port + " (" + connector.getScheme() + ")");
        }
        return ports.toString();
    }

}
