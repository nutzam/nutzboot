package org.nutz.boot.starter.undertow;

import java.io.File;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.DispatcherType;

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
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LifeCycle;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;

/**
 * Undertow 启动器
 * @author qinerg(qinerg@gmail.com)
 */
public class UndertowStarter implements ClassLoaderAware, IocAware, ServerFace, LifeCycle, AppContextAware {

	private static final Log log = Logs.get();

	protected static final String PRE = "undertow.";

	@PropDoc(group = "undertow", value = "监听的ip地址", defaultValue = "0.0.0.0")
	public static final String PROP_HOST = PRE + "host";

	@PropDoc(group = "undertow", value = "监听的端口", defaultValue = "8080", type = "int")
	public static final String PROP_PORT = PRE + "port";

	@PropDoc(group = "undertow", value = "上下文路径", defaultValue = "/")
	public static final String PROP_CONTEXT_PATH = PRE + "contextPath";

	@PropDoc(group = "undertow", value = "session过期时间", defaultValue = "20")
	public static final String PROP_SESSION = PRE + "session";

	@PropDoc(group = "undertow", value = "过滤器顺序", defaultValue = "whale,druid,shiro,nutz")
	public static final String PROP_WEB_FILTERS_ORDER = "web.filters.order";
	
	@PropDoc(group = "undertow", value = "静态文件路径", defaultValue = "/static/")
	public static final String PROP_STATIC_PATH = PRE + "staticPath";

	protected Undertow server;
	protected ClassLoader classLoader;
	protected Ioc ioc;
	protected AppContext appContext;

	protected Builder builder = Undertow.builder();
	protected DeploymentInfo deployment;

	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public boolean isRunning() {
		return !server.getWorker().isShutdown();
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
		PropertiesProxy conf = appContext.getConfigureLoader().get();

		String contextPath = conf.get(PROP_CONTEXT_PATH, "/");
		deployment = Servlets.deployment().setDeploymentName("nb").setClassLoader(classLoader).setEagerFilterInit(true).setSecurityDisabled(true);
		deployment.setContextPath(contextPath).setDefaultSessionTimeout(conf.getInt(PROP_SESSION, 20) * 60);

		String staticPath = conf.get(PROP_STATIC_PATH, "static");
		File resRootDir = Files.findFile(staticPath);
		if (resRootDir != null && resRootDir.isDirectory()) {
			deployment.setResourceManager(new FileResourceManager(resRootDir, 1024));
		} else {
			deployment.setResourceManager(new ClassPathResourceManager(classLoader, staticPath));
		}

		// 添加其他starter提供的WebXXXX服务
		Map<String, WebFilterFace> filters = new HashMap<>();
		for (Object object : appContext.getStarters()) {
			if (object instanceof WebFilterFace) {
				WebFilterFace webFilter = (WebFilterFace) object;
				filters.put(webFilter.getName(), webFilter);
			}
			if (object instanceof WebServletFace) {
				WebServletFace webServlet = (WebServletFace) object;
				addServlet(webServlet);
			}
			if (object instanceof WebEventListenerFace) {
				WebEventListenerFace contextListener = (WebEventListenerFace) object;
				addEventListener(contextListener);
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

		deployment.addWelcomePages("index.html", "index.htm", "index.do");

		DeploymentManager manager = Servlets.defaultContainer().addDeployment(deployment);
		manager.deploy();

		HttpHandler servletHandler = manager.start();
		PathHandler pathHandler;
		if ("/".equals(contextPath)) {
			pathHandler = Handlers.path(servletHandler);
		} else {
			pathHandler = Handlers.path(Handlers.redirect(contextPath)).addPrefixPath(contextPath, servletHandler);
		}
		builder.addHttpListener(conf.getInt(PROP_PORT, 8080), conf.get(PROP_HOST, "0.0.0.0")).setHandler(pathHandler);

		server = builder.build();
	}
	
	public void addServlet(WebServletFace webServlet) {
	    if (webServlet == null || webServlet.getServlet() == null)
            return;
	    
	    ServletInfo servlet = new ServletInfo(webServlet.getName(), webServlet.getServlet().getClass());
        Iterator<Map.Entry<String, String>> entries = webServlet.getInitParameters().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            servlet.addInitParam(entry.getKey(), entry.getValue());
        }
        servlet.addMapping(webServlet.getPathSpec());
        log.debugf("add servlet name=%s pathSpec=%s", webServlet.getName(), webServlet.getPathSpec());
        deployment.addServlet(servlet);
	}

	public void addFilter(WebFilterFace webFilter) {
		if (webFilter == null || webFilter.getFilter() == null)
			return;

		log.debugf("add filter name=%s pathSpec=%s", webFilter.getName(), webFilter.getPathSpec());
		FilterInfo filter = new FilterInfo(webFilter.getName(), webFilter.getFilter().getClass());
		Iterator<Map.Entry<String, String>> entries = webFilter.getInitParameters().entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			filter.addInitParam(entry.getKey(), entry.getValue());
		}
		deployment.addFilter(filter)
				.addFilterUrlMapping(webFilter.getName(), webFilter.getPathSpec(), DispatcherType.REQUEST)
				.addFilterUrlMapping(webFilter.getName(), webFilter.getPathSpec(), DispatcherType.FORWARD);
	}
	
	public void addEventListener(WebEventListenerFace webEventListener) {
		if (webEventListener.getEventListener() == null)
			return;

		EventListener et = webEventListener.getEventListener();
		ListenerInfo listener = new ListenerInfo(et.getClass());
		listener.setInstanceFactory(new InstanceFactory<EventListener>() {
			public InstanceHandle<EventListener> createInstance() throws InstantiationException {
				return new InstanceHandle<EventListener>() {
					public EventListener getInstance() {
						return et;
					}
					public void release() {
					}
				};
			}
		});
		deployment.addListener(listener);
	}

	public void fetch() throws Exception {
	}

	public void depose() throws Exception {
	}

}
