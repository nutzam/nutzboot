package org.nutz.boot.starter.jetty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

public class JettyStarter implements ClassLoaderAware, IocAware, ServerFace, LifeCycle, AppContextAware {
	
	private static final Log log = Logs.get();
	
	protected static final String PRE = "jetty.";

	@PropDoc(group="jetty", value="监听的ip地址", defaultValue="0.0.0.0")
	public static final String PROP_HOST = PRE + "host";

	@PropDoc(group="jetty", value="监听的端口", defaultValue="8080", type="int")
	public static final String PROP_PORT = PRE + "port";
	
	@PropDoc(group="jetty", value="空闲时间,单位毫秒", defaultValue="300000", type="int")
	public static final String PROP_IDLE_TIMEOUT = PRE + "http.idleTimeout";
	
	@PropDoc(group="jetty", value="上下文路径", defaultValue="/")
	public static final String PROP_CONTEXT_PATH = PRE + "contextPath";
	
	@PropDoc(group="jetty", value="表单最大尺寸", defaultValue="1gb", type="int")
	public static final String PROP_MAX_FORM_CONTENT_SIZE = PRE + "maxFormContentSize";
	
	@PropDoc(group="web", value="过滤器顺序", defaultValue="whale,druid,shiro,nutz")
	public static final String PROP_WEB_FILTERS_ORDER = "web.filters.order";
    
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
        // copy and modify from nutz-web
        // 创建基础服务器
        server = new Server(new QueuedThreadPool(Lang.isAndroid ? 50 : 500));
        ServerConnector connector= new ServerConnector(server);
        PropertiesProxy conf = appContext.getConfigureLoader().get();
        connector.setHost(conf.get(PROP_HOST, "0.0.0.0"));
        connector.setPort(conf.getInt(PROP_PORT, 8080));
        connector.setIdleTimeout(conf.getInt(PROP_IDLE_TIMEOUT, 300*1000));
        server.setConnectors(new Connector[]{connector});
        
        
        // 设置应用上下文
        wac = new WebAppContext();
        wac.setContextPath(conf.get(PROP_CONTEXT_PATH, "/"));
        //wac.setExtractWAR(false);
        //wac.setCopyWebInf(true);
        //wac.setProtectedTargets(new String[]{"/java", "/javax", "/org", "/net", "/WEB-INF", "/META-INF"});
        wac.setTempDirectory(new File("./tmp").getAbsoluteFile());
        wac.setClassLoader(classLoader);
        wac.setConfigurationDiscovered(true);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            wac.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        }
        List<Resource> resources = new ArrayList<>();
        for (String resourcePath : Arrays.asList("static/", "webapp/")) {
        	File f = new File(resourcePath);
        	if (f.exists())
        		resources.add(Resource.newResource(f));
        	Enumeration<URL> urls = appContext.getClassLoader().getResources(resourcePath);
        	while(urls.hasMoreElements()) {
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
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", conf.getInt(PROP_MAX_FORM_CONTENT_SIZE, 1024*1024*1024));
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        
        ServerContainer sc = WebSocketServerContainerInitializer.configureContext(wac);
        for (Class<?> klass : Scans.me().scanPackage(appContext.getPackage())) {
			if (klass.getAnnotation(ServerEndpoint.class) != null) {
				sc.addEndpoint(klass);
			}
		}

        // 添加其他starter提供的WebXXXX服务
        Map<String, WebFilterFace> filters = new HashMap<>();
        for (Object object : appContext.getStarters()) {
            if (object instanceof WebFilterFace) {
                WebFilterFace webFilter = (WebFilterFace)object;
                filters.put(webFilter.getName(), webFilter);
            }
            if (object instanceof WebServletFace) {
                WebServletFace webServlet = (WebServletFace)object;
                if (webServlet.getServlet() == null)
                	continue;
                ServletHolder holder = new ServletHolder(webServlet.getServlet());
                holder.setName(webServlet.getName());
                holder.setInitParameters(webServlet.getInitParameters());
                wac.addServlet(holder, webServlet.getPathSpec());
            }
            if (object instanceof WebEventListenerFace) {
            	WebEventListenerFace contextListener = (WebEventListenerFace)object;
                if (contextListener.getEventListener() == null)
                	continue;
            	wac.addEventListener(contextListener.getEventListener());
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
    
    public void addFilter(WebFilterFace webFilter) {
    	if (webFilter == null || webFilter.getFilter() == null)
			return;
    	log.debugf("add filter name=%s pathSpec=%s", webFilter.getName(), webFilter.getPathSpec());
        FilterHolder holder = new FilterHolder(webFilter.getFilter());
        holder.setName(webFilter.getName());
        holder.setInitParameters(webFilter.getInitParameters());
        wac.addFilter(holder, webFilter.getPathSpec(), webFilter.getDispatches());
    }

    public void fetch() throws Exception {
    }

    public void depose() throws Exception {
    }

}
