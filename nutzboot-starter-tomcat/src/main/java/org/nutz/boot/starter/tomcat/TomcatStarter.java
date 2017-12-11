package org.nutz.boot.starter.tomcat;


import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.servlet.JspServlet;
import org.apache.naming.ContextBindings;
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
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tomcat 启动器
 *
 * @author benjobs (benjobs@qq.com)
 */
public class TomcatStarter extends AbstractServletContainerFactory implements ClassLoaderAware, IocAware, ServerFace, LifeCycle, AppContextAware {

    private static final Log logger = Logs.get();

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
    protected TomcatContext tomcatContext;

    protected ClassLoader classLoader;
    protected Ioc ioc;
    protected AppContext appContext;
    private PropertiesProxy conf;


    private final Map<Service, Connector[]> serviceConnectors = new HashMap<Service, Connector[]>();

    private final Object monitor = new Object();

    private static final AtomicInteger containerCounter = new AtomicInteger(-1);

    private Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Charset uriEncoding = DEFAULT_CHARSET;

    private final boolean autoStart = true;

    private volatile boolean started;

    //--getConf---
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

    @Override
    public void init() {

        synchronized (this.monitor) {

            this.conf = appContext.getConfigureLoader().get();

            this.tomcat = new Tomcat();

            File baseDir = createTempDir("tomcat");
            this.tomcat.setBaseDir(baseDir.getAbsolutePath());

            Connector connector = new Connector(this.PROP_PROTOCOL);
            connector.setPort(getPort());
            connector.setURIEncoding(getUriEncoding().name());

            //maxThread
            this.tomcat.getService().addConnector(connector);
            StandardThreadExecutor executor = new StandardThreadExecutor();
            executor.setMaxThreads(getMaxThread());
            connector.getService().addExecutor(executor);

            // If ApplicationContext is slow to start we want Tomcat not to bind to the socket
            // prematurely...
            connector.setProperty("bindOnInit", "false");

            this.tomcat.setConnector(connector);

            this.tomcat.setHostname(getHost());
            this.tomcat.getHost().setAutoDeploy(false);
            this.tomcat.getEngine().setBackgroundProcessorDelay(30);

            prepareContext(this.tomcat.getHost());

            try {

                addInstanceIdToEngineName();

                removeServiceConnectors();

                this.tomcat.start();

                this.nutzSupport();

                rethrowDeferredStartupExceptions();

                Context context = findContext();
                try {
                    ContextBindings.bindClassLoader(
                            context,
                            getNamingToken(context),
                            getClass().getClassLoader()
                    );
                } catch (NamingException ex) {
                }
                startDaemonAwaitThread();
            } catch (Exception ex) {
                this.containerCounter.decrementAndGet();
                throw new EmbeddedServletContainerException("Unable to start embedded Tomcat", ex);
            }

        }

    }

    @Override
    public void start() throws EmbeddedServletContainerException {
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            try {
                addPreviouslyRemovedConnectors();
                Connector connector = this.tomcat.getConnector();
                if (connector != null && this.autoStart) {
                    startConnector(connector);
                }
                checkThatConnectorsHaveStarted();
                this.started = true;
                logger.info("Tomcat started on port(s): " + getPortsDescription(true));
            } catch (ConnectorStartFailedException ex) {
                stopSilently();
                throw ex;
            } catch (Exception ex) {
                throw new EmbeddedServletContainerException(
                        "Unable to start embedded Tomcat servlet container", ex);
            } finally {
                Context context = findContext();
                ContextBindings.unbindClassLoader(context, getNamingToken(context),
                        getClass().getClassLoader());
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

    private void prepareContext(Host host) {

         File docBase = getValidDocumentRoot(getStaticPath());
         docBase = (docBase != null ? docBase : createTempDir("tomcat-docbase"));

        this.tomcatContext = new TomcatContext();
        this.tomcatContext.setName(getContextPath());
        this.tomcatContext.setPath(getContextPath());
        this.tomcatContext.setDocBase(docBase.getAbsolutePath());
        this.tomcatContext.addLifecycleListener(new Tomcat.FixContextListener());
        this.tomcatContext.setParentClassLoader(ClassUtils.getDefaultClassLoader());

        for (String welcomeFile : Arrays.asList("index.html", "index.htm", "index.jsp", "index.do")) {
            this.tomcatContext.addWelcomeFile(welcomeFile);
        }

        resetDefaultLocaleMapping();

        try {
            this.tomcatContext.setUseRelativeRedirects(false);
        } catch (NoSuchMethodError ex) {
            // Tomcat is < 8.0.30. Continue
        }
        WebappLoader loader = new WebappLoader(tomcatContext.getParentClassLoader());
        loader.setLoaderClass(TomcatWebappClassLoader.class.getName());
        loader.setDelegate(true);
        this.tomcatContext.setLoader(loader);

        //注册defaultServlet
        addDefaultServlet();

        //注册JspServlet
        addJspServlet();

        this.tomcatContext.addLifecycleListener(new StoreMergedWebXmlListener());

        this.tomcatContext.addLifecycleListener(new LifecycleListener() {
            @Override
            public void lifecycleEvent(LifecycleEvent event) {
                if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                    TomcatResources.get(TomcatStarter.this.tomcatContext)
                            .addResourceJars(getUrlsOfJarsWithMetaInfResources());
                }
            }
        });

        host.addChild(tomcatContext);
    }

    private void nutzSupport() {
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
                this.tomcatContext.addApplicationEventListener(contextListener.getEventListener());
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

    private void addJspServlet() {
        Wrapper jspServlet = this.tomcatContext.createWrapper();
        jspServlet.setName("jsp");
        jspServlet.setServletClass(JspServlet.class.getName());
        jspServlet.addInitParameter("fork", "false");
        jspServlet.addInitParameter("development", "false");
        jspServlet.setLoadOnStartup(3);
        this.tomcatContext.addChild(jspServlet);
        addServletMapping("*.jsp", "jsp");
        addServletMapping("*.jspx", "jsp");
    }

    private void addServlet(WebServletFace webServlet) {
        logger.debugf("[NutzBoot] add servlet name=%s pathSpec=%s", webServlet.getName(), webServlet.getPathSpec());

        Wrapper servlet = tomcatContext.createWrapper();
        servlet.setName(webServlet.getName());
        servlet.setServletClass(webServlet.getServlet().getClass().getName());

        for (Map.Entry<String, String> entry : webServlet.getInitParameters().entrySet()) {
            servlet.addInitParameter(entry.getKey(), entry.getValue());
        }
        servlet.setOverridable(true);
        tomcatContext.addChild(servlet);
        addServletMapping(webServlet.getPathSpec(), webServlet.getName());
    }

    private void addFilter(WebFilterFace filterFace) {
        if (filterFace == null || filterFace.getFilter() == null) {
            return;
        }
        logger.debugf("[NutzBoot] add filter name=%s pathSpec=%s", filterFace.getName(), filterFace.getPathSpec());
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

    private Context findContext() {
        for (Container child : this.tomcat.getHost().findChildren()) {
            if (child instanceof Context) {
                return (Context) child;
            }
        }
        throw new IllegalStateException("The host does not contain a Context");
    }

    private void addInstanceIdToEngineName() {
        int instanceId = containerCounter.incrementAndGet();
        if (instanceId > 0) {
            Engine engine = this.tomcat.getEngine();
            engine.setName(engine.getName() + "-" + instanceId);
        }
    }

    private void removeServiceConnectors() {
        for (Service service : this.tomcat.getServer().findServices()) {
            Connector[] connectors = service.findConnectors().clone();
            this.serviceConnectors.put(service, connectors);
            for (Connector connector : connectors) {
                service.removeConnector(connector);
            }
        }
    }

    private void rethrowDeferredStartupExceptions() {
        Container[] children = this.tomcat.getHost().findChildren();
        for (Container container : children) {
            if (!LifecycleState.STARTED.equals(container.getState())) {
                throw new IllegalStateException(container + " failed to start");
            }
        }
    }

    private void addPreviouslyRemovedConnectors() {
        Service[] services = this.tomcat.getServer().findServices();
        for (Service service : services) {
            Connector[] connectors = this.serviceConnectors.get(service);
            if (connectors != null) {
                for (Connector connector : connectors) {
                    service.addConnector(connector);
                    if (!this.autoStart) {
                        stopProtocolHandler(connector);
                    }
                }
                this.serviceConnectors.remove(service);
            }
        }
    }

    private void stopProtocolHandler(Connector connector) {
        try {
            connector.getProtocolHandler().stop();
        } catch (Exception ex) {
            logger.error("Cannot pause connector: ", ex);
        }
    }

    private void startConnector(Connector connector) {
        try {
            for (Container child : this.tomcat.getHost().findChildren()) {
                if (child instanceof TomcatContext) {
                    ((TomcatContext) child).deferredLoadOnStartup();
                }
            }
        } catch (Exception ex) {
            logger.error("Cannot start connector: ", ex);
            throw new EmbeddedServletContainerException(
                    "Unable to start embedded Tomcat connectors", ex);
        }
    }

    private void checkThatConnectorsHaveStarted() {
        for (Connector connector : this.tomcat.getService().findConnectors()) {
            if (LifecycleState.FAILED.equals(connector.getState())) {
                throw new ConnectorStartFailedException(connector.getPort());
            }
        }
    }

    private void resetDefaultLocaleMapping() {
        this.tomcatContext.addLocaleEncodingMappingParameter(Locale.ENGLISH.toString(), DEFAULT_CHARSET.displayName());
        this.tomcatContext.addLocaleEncodingMappingParameter(Locale.FRENCH.toString(), DEFAULT_CHARSET.displayName());
    }

    private void addServletMapping(String pattern, String name) {
        this.tomcatContext.addServletMappingDecoded(pattern, name);
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


    public Charset getUriEncoding() {
        return uriEncoding;
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
            Assert.state(stream != null, "Unable to read empty web.xml");
            try {
                try {
                    return StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
                } finally {
                    stream.close();
                }
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

    }

    private Object getNamingToken(Context context) {
        try {
            return context.getNamingToken();
        } catch (NoSuchMethodError ex) {
            // Use the context itself on Tomcat 7
            return context;
        }

    }
}
