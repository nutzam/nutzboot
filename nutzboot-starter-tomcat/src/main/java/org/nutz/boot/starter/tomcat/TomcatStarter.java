package org.nutz.boot.starter.tomcat;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
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

    @Override
    public void init() throws LifecycleException {

        this.tomcat = new Tomcat();

        // 貌似Tomcat死活需要一个temp目录
        File baseDir = new File("./temp");
        this.tomcat.setBaseDir(baseDir.getAbsolutePath());

        // 当前支持Http就够了
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

        prepareContext(this.tomcat.getHost());

        this.nutzSupport();
    }

    public void start() throws LifecycleException {
        this.tomcat.start();
    }

    public void stop() throws LifecycleException {
        this.tomcat.stop();
        this.tomcat = null;
    }

    public boolean isRunning() {
        return true;
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
        if (this.tomcat != null)
            this.tomcat.stop();
    }

    private void prepareContext(Host host) {
        this.tomcatContext = new StandardContext();
        this.tomcatContext.setName(getContextPath());
        this.tomcatContext.setPath(getContextPath());
        this.tomcatContext.addLifecycleListener(new Tomcat.FixContextListener());
        this.tomcatContext.setParentClassLoader(classLoader);

        for (String welcomeFile : Arrays.asList("index.html",
                                                "index.htm",
                                                "index.jsp",
                                                "index.do")) {
            this.tomcatContext.addWelcomeFile(welcomeFile);
        }

        this.tomcatContext.setUseRelativeRedirects(false);

        // 注册defaultServlet
        addDefaultServlet();

        // 注册JspServlet
        addJspServlet();

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

    private void addJspServlet() {
        // 暂不支持jsp了
        // Wrapper jspServlet = this.tomcatContext.createWrapper();
        // jspServlet.setName("jsp");
        // jspServlet.setServletClass(JspServlet.class.getName());
        // jspServlet.addInitParameter("fork", "false");
        // jspServlet.addInitParameter("development", "false");
        // jspServlet.setLoadOnStartup(3);
        // this.tomcatContext.addChild(jspServlet);
        // addServletMapping("*.jsp", "jsp");
        // addServletMapping("*.jspx", "jsp");
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
        tomcatContext.addChild(servlet);
        addServletMapping(webServlet.getPathSpec(), webServlet.getName());
    }

    private void addFilter(WebFilterFace filterFace) {
        if (filterFace == null || filterFace.getFilter() == null) {
            return;
        }
        log.debugf("[NutzBoot] add filter name=%s pathSpec=%s",
                   filterFace.getName(),
                   filterFace.getPathSpec());
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
        filterMap.addURLPattern(filterFace.getPathSpec());
        filterMap.setFilterName(filterFace.getName());
        this.tomcatContext.addFilterMap(filterMap);
    }

    private void addServletMapping(String pattern, String name) {
        this.tomcatContext.addServletMappingDecoded(pattern, name);
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
