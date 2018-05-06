package org.nutz.boot.starter.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.servlet3.AbstractServletContainerStarter;
import org.nutz.boot.starter.servlet3.NbServletContextListener;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * tomcat 启动器
 * <p>
 * 十步杀一人 千里不留行 事了扶衣去 深藏功与名
 * </p>
 *
 * @author benjobs (benjobs@qq.com)
 * @author wendal (wendal1985@gmail.com)
 */
@IocBean
public class TomcatStarter extends AbstractServletContainerStarter implements ServerFace {

    private static final Log log = Logs.get();

    protected static final String PRE = "tomcat.";

    @PropDoc(group = "tomcat", value = "监听的ip地址", defaultValue = "0.0.0.0")
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(group = "tomcat", value = "监听的端口", defaultValue = "8080", type = "int")
    public static final String PROP_PORT = PRE + "port";

    @PropDoc(group = "tomcat", value = "上下文路径")
    public static final String PROP_CONTEXT_PATH = PRE + "contextPath";

    @PropDoc(value = "Session空闲时间,单位分钟", defaultValue = "30", type = "int")
    public static final String PROP_SESSION_TIMEOUT = "web.session.timeout";

    @PropDoc(group = "tomcat", value = "静态文件路径", defaultValue = "static")
    public static final String PROP_STATIC_PATH = PRE + "staticPath";

    @PropDoc(value = "POST表单最大尺寸", defaultValue = "64 * 1024 * 1024")
    public static final String PROP_MAX_POST_SIZE = PRE + "maxPostSize";

    private static final String PROP_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    private static final Charset DEFAULT_CHARSET = Encoding.CHARSET_UTF8;

    protected Tomcat tomcat;

    protected StandardContext tomcatContext;

    private final AtomicInteger containerCounter = new AtomicInteger(-1);

    private final Object monitor = new Object();

    private volatile boolean started;

    // tomcat await thread
    private Thread tomcatAwaitThread;

    @Override
    public void init() throws LifecycleException {

        this.tomcat = new Tomcat();

        File baseDir = createTempDir("tomcat");
        this.tomcat.setBaseDir(baseDir.getAbsolutePath());

        Connector connector = new Connector(PROP_PROTOCOL);
        connector.setPort(getPort());
        connector.setURIEncoding(DEFAULT_CHARSET.name());
        connector.setMaxPostSize(conf.getInt(PROP_MAX_POST_SIZE, 64 * 1024 * 1024));

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
            tomcatAwaitThread = new Thread("container-" + (containerCounter.get())) {
                @Override
                public void run() {
                    TomcatStarter.this.tomcat.getServer().await();
                }
            };
            tomcatAwaitThread.setContextClassLoader(getClass().getClassLoader());
            tomcatAwaitThread.setDaemon(false);
            tomcatAwaitThread.start();
            this.started = true;
        }
    }

    public void stop() throws LifecycleException {
        synchronized (this.monitor) {
            if (started) {
                this.tomcat.stop();
                this.tomcat = null;
                this.tomcatAwaitThread.interrupt();
                this.started = false;
            }
        }
    }

    public boolean isRunning() {
        return this.started;
    }

    public void depose() throws LifecycleException {
        if (this.tomcat != null) {
            this.stop();
        }
    }

    private void prepareContext() {
        File docBase = Files.findFile("static/");

        docBase = (docBase != null && docBase.isDirectory()) ? docBase : createTempDir("tomcat-docbase");

        this.tomcatContext = new StandardContext();
        this.tomcatContext.setDocBase(docBase.getAbsolutePath());
        this.tomcatContext.setName(getContextPath());
        this.tomcatContext.setPath(getContextPath());
        this.tomcatContext.setDelegate(false);
        this.tomcatContext.addLifecycleListener(new Tomcat.FixContextListener());
        this.tomcatContext.setParentClassLoader(classLoader);
        this.tomcatContext.setSessionTimeout(getSessionTimeout() / 60);
        this.tomcatContext.addLifecycleListener(new StoreMergedWebXmlListener());
        StandardRoot sr = new StandardRoot(this.tomcatContext);
        sr.addPreResources(new ClasspathResourceSet(sr, "static/"));
        this.tomcatContext.setResources(sr);

        try {
            this.tomcatContext.setUseRelativeRedirects(false);
        }
        catch (NoSuchMethodError ex) {
            // Tomcat is < 8.0.30. Continue
        }

        for (String welcomeFile : Arrays.asList("index.html", "index.htm", "index.jsp", "index.do")) {
            this.tomcatContext.addWelcomeFile(welcomeFile);
        }

        // 注册defaultServlet
        addDefaultServlet();

        addNutzSupport();

        this.tomcat.getHost().addChild(tomcatContext);
    }

    private void addNutzSupport() {
        NbServletContextListener nbsc = appContext.getIoc().get(NbServletContextListener.class);
        this.tomcatContext.addApplicationLifecycleListener(nbsc);
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

    private void addServletMapping(String pattern, String name) {
        this.tomcatContext.addServletMappingDecoded(pattern, name);
    }

    private File createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + getPort());
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            log.debug("tempDir = " + tempDir);
            return tempDir;
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
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
                    InputStreamReader reader = new InputStreamReader(stream, DEFAULT_CHARSET);
                    char[] buffer = new char[1024 * 4];
                    int bytesRead = -1;
                    while ((bytesRead = reader.read(buffer)) != -1) {
                        out.append(buffer, 0, bytesRead);
                    }
                    return out.toString();
                }
                finally {
                    stream.close();
                }
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

    }

    public int getMaxThread() {
        return Lang.isAndroid ? 50 : 500;
    }

    public class ClasspathResourceSet extends EmptyResourceSet {

        protected String prefix;

        public ClasspathResourceSet(WebResourceRoot root, String prefix) {
            super(root);
            this.prefix = prefix;
        }

        public boolean isReadOnly() {
            return true;
        }

        public URL getBaseUrl() {
            return appContext.getClassLoader().getResource(prefix);
        }

        public WebResource getResource(String path) {
            return super.getResource(path);
        }

        @Override
        public boolean getStaticOnly() {
            return true;
        }
    }

    public String getContextPath() {
        String cp = super.getContextPath();
        return "/".equals(cp) ? "" : cp;
    }

    protected String getConfigurePrefix() {
        return PRE;
    }
}
