package org.nutz.boot.starter.undertow;

import java.io.File;
import java.util.EventListener;
import java.util.zip.Deflater;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.servlet3.AbstractServletContainerStarter;
import org.nutz.boot.starter.servlet3.NbServletContextListener;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * Undertow 启动器
 * 
 * @author qinerg(qinerg@gmail.com)
 */
@IocBean
public class UndertowStarter extends AbstractServletContainerStarter implements ServerFace {

    private static final Log log = Logs.get();

    protected static final String PRE = "undertow.";

    @PropDoc(value = "监听的ip地址", defaultValue = "0.0.0.0")
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(value = "监听的端口", defaultValue = "8080", type = "int")
    public static final String PROP_PORT = PRE + "port";

    @PropDoc(value = "上下文路径", defaultValue = "/")
    public static final String PROP_CONTEXT_PATH = PRE + "contextPath";

    @PropDoc(value = "Session空闲时间,单位分钟", defaultValue = "30", type = "int")
    public static final String PROP_SESSION_TIMEOUT = "web.session.timeout";

    @PropDoc(value = "静态文件路径", defaultValue = "static/")
    public static final String PROP_STATIC_PATH = PRE + "staticPath";

    @PropDoc(value = "是否启用gzip", defaultValue = "false")
    public static final String PROP_GZIP_ENABLE = PRE + "gzip.enable";

    @PropDoc(value = "gzip压缩级别", defaultValue = "-1")
    public static final String PROP_GZIP_LEVEL = PRE + "gzip.level";

    @PropDoc(value = "gzip压缩最小触发大小", defaultValue = "512")
    public static final String PROP_GZIP_MIN_CONTENT_SIZE = PRE + "gzip.minContentSize";

    protected Undertow server;
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

    public void init() throws Exception {
        String contextPath = getContextPath();

        deployment = Servlets.deployment().setDeploymentName("nb").setClassLoader(classLoader).setEagerFilterInit(true).setSecurityDisabled(true);
        deployment.setContextPath(contextPath).setDefaultSessionTimeout(getSessionTimeout());

        ComboResourceManager resourceManager = new ComboResourceManager();
        for (String path : getResourcePaths()) {
            if (new File(path).exists())
                resourceManager.add(new FileResourceManager(new File(path), 1024));
            try {
                resourceManager.add(new ClassPathResourceManager(classLoader, path));
            }
            catch (Throwable e) {
                // 不合法的,就跳过吧
            }
        }
        deployment.setResourceManager(resourceManager);

        addNutzSupport();
        addWebSocketSupport();

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
        HttpHandler handler = pathHandler;
        if (conf.getBoolean(PROP_GZIP_ENABLE, false)) {
            ContentEncodingRepository repo = new ContentEncodingRepository();
            GzipEncodingProvider gzip = new GzipEncodingProvider(conf.getInt(PROP_GZIP_LEVEL, Deflater.DEFAULT_COMPRESSION));
            int minContentSize = conf.getInt(PROP_GZIP_MIN_CONTENT_SIZE, 512);
            if (minContentSize > 0) {
                repo.addEncodingHandler("gzip", gzip, 100, Predicates.minContentSize(minContentSize));
            }
            else {
                repo.addEncodingHandler("gzip", gzip, 100);
            }
            handler = new EncodingHandler(pathHandler, repo);
        }
        builder.addHttpListener(getPort(), getHost()).setHandler(handler);

        server = builder.build();
    }

    private void addNutzSupport() {
        NbServletContextListener nbsc = ioc.get(NbServletContextListener.class);
        ImmediateInstanceFactory<EventListener> factory = new ImmediateInstanceFactory<EventListener>(nbsc);
        ListenerInfo listener = new ListenerInfo(nbsc.getClass(), factory);
        deployment.addListener(listener);
    }

    private void addWebSocketSupport() {
        try {
            WebSocketSupport.addWebSocketSupport(deployment, appContext.getPackage());
        }
        catch (Error e) {
            log.info("Not find undertow-websockets-jsr, websocket disable.");
        }
    }

    protected String getConfigurePrefix() {
        return PRE;
    }

}
