package org.nutz.boot.starter.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

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
import org.apache.catalina.webresources.AbstractResource;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.FileResource;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.MonitorObject;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.servlet3.AbstractServletContainerStarter;
import org.nutz.boot.starter.servlet3.NbServletContextListener;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
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
public class TomcatStarter extends AbstractServletContainerStarter implements ServerFace, MonitorObject {

    private static final Log log = Logs.get();

    protected static final String PRE = "tomcat.";

    @PropDoc(value = "监听的ip地址", defaultValue = "0.0.0.0")
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(value = "监听的端口", defaultValue = "8080", type = "int")
    public static final String PROP_PORT = PRE + "port";

    @PropDoc(value = "上下文路径")
    public static final String PROP_CONTEXT_PATH = PRE + "contextPath";

    @PropDoc(value = "Session空闲时间,单位分钟", defaultValue = "30", type = "int")
    public static final String PROP_SESSION_TIMEOUT = "web.session.timeout";

    @PropDoc(value = "静态文件路径", defaultValue = "static")
    public static final String PROP_STATIC_PATH = PRE + "staticPath";

    @PropDoc(value = "本地静态文件路径")
    public static final String PROP_STATIC_PATH_LOCAL = PRE + "staticPathLocal";

    @PropDoc(value = "POST表单最大尺寸", defaultValue = "64 * 1024 * 1024")
    public static final String PROP_MAX_POST_SIZE = PRE + "maxPostSize";
    
    @PropDoc(value = "最大线程数", defaultValue = "256")
    public static final String PROP_EXECUTOR_MAX_THREADS = PRE + "executor.maxThreads";

    @PropDoc(value = "自定义404页面,同理,其他状态码也是支持的")
    public static final String PROP_PAGE_404 = PRE + "page.404";
    
    @PropDoc(value = "自定义java.lang.Throwable页面,同理,其他异常也支持")
    public static final String PROP_PAGE_THROWABLE = PRE + "page.java.lang.Throwable";

    @PropDoc(value = "WelcomeFile列表", defaultValue="index.html,index.htm,index.do")
    public static final String PROP_WELCOME_FILES = PRE + "welcome_files";
    
    @PropDoc(value = "自定义Connector配置群")
    public static final String PROP_CONNECTOR_CONFS = PRE + "connector.*";

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

        updateMonitorValue("http.port", getPort());
        updateMonitorValue("http.host", getHost());
        
        this.tomcat = new Tomcat();

        File baseDir = createTempDir("tomcat");
        this.tomcat.setBaseDir(baseDir.getAbsolutePath());

        Connector connector = new Connector(PROP_PROTOCOL);
        connector.setPort(getPort());
        connector.setURIEncoding(DEFAULT_CHARSET.name());
        connector.setMaxPostSize(conf.getInt(PROP_MAX_POST_SIZE, 64 * 1024 * 1024));
        String connectorKey = PRE + "connector.";
        for (String key : conf.keys()) {
            if (key.startsWith(connectorKey)) {
                String k = key.substring(connectorKey.length());
                String v = conf.get(key);
                connector.setProperty(k, v);
            }
        }

        // 设置一下最大线程数
        this.tomcat.getService().addConnector(connector);
        StandardThreadExecutor executor = new StandardThreadExecutor();
        executor.setMaxThreads(getMaxThread());
        connector.getService().addExecutor(executor);
        updateMonitorValue("maxThread", executor.getMaxThreads());

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
            if (System.getProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE") == null) {
                System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
            }
            this.tomcat.start();
            tomcatAwaitThread = new Thread("container-" + (containerCounter.get())) {
                @Override
                public void run() {
                    TomcatStarter.this.tomcat.getServer().await();
                }
            };
            tomcatAwaitThread.setContextClassLoader(getClass().getClassLoader());
            tomcatAwaitThread.setDaemon(true);
            tomcatAwaitThread.start();
            this.started = true;
        }
        if (log.isDebugEnabled())
            log.debug("Tomcat monitor props:\r\n"+getMonitorForPrint());
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

    protected void prepareContext() {
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
        
        updateMonitorValue("contextPath", super.getContextPath());
        updateMonitorValue("sessionTimeout", getSessionTimeout());
        
        StandardRoot sr = new StandardRoot(this.tomcatContext);
        if (!Strings.isBlank(conf.get(PROP_STATIC_PATH_LOCAL))) {
            File local = new File(conf.get(PROP_STATIC_PATH_LOCAL));
            if (local.exists()) {
                log.debug("add local path=" + local.getAbsolutePath());
                sr.addPreResources(new DirResourceSet(sr, "/", local.getAbsolutePath(), "/"));
            }
            else {
                log.debug("local path=" + local + " not exists, skip.");
            }
        }
        for (String resourcePath : getResourcePaths()) {
            if ("static".equals(resourcePath) || "static/".equals(resourcePath)) {
                if (new File(resourcePath).exists()) {
                    sr.addPreResources(new DirResourceSet(sr, "/", new File(resourcePath).getAbsolutePath(), "/"));
                }
                sr.addPreResources(new ClasspathResourceSet(sr, "static"));
            }
            else if ("webapp".equals(resourcePath) || "webapp/".equals(resourcePath)) {
                sr.addPreResources(new ClasspathResourceSet(sr, "webapp"));
            }
        }
        this.tomcatContext.setResources(sr);

        try {
            this.tomcatContext.setUseRelativeRedirects(false);
        }
        catch (NoSuchMethodError ex) {
            // Tomcat is < 8.0.30. Continue
        }

        for (String welcomeFile : getWelcomeFiles()) {
            this.tomcatContext.addWelcomeFile(welcomeFile);
        }

        for (Map.Entry<String, String> en : getErrorPages().entrySet()) {
            ErrorPage page = new ErrorPage();
            page.setLocation(en.getValue());
            String key = en.getKey();
            if (Strings.isNumber(key)) {
                log.debugf("add error page code=%s location=%s", key, en.getValue());
                page.setErrorCode(key);
            }
            else {
                log.debugf("add error page Exception=%s location=%s", key, en.getValue());
                page.setExceptionType(key);
            }
            this.tomcatContext.addErrorPage(page);
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
        return Lang.isAndroid ? 50 : conf.getInt(PROP_EXECUTOR_MAX_THREADS, 256);
    }

    public class ClasspathResourceSet extends EmptyResourceSet {

        protected org.apache.juli.logging.Log LOG = LogFactory.getLog(ClasspathResourceSet.class);

        protected String prefix;

        private WebResourceRoot root;

        public ClasspathResourceSet(WebResourceRoot root, String prefix) {
            super(root);
            this.prefix = prefix;
            this.root = root;
        }

        public boolean isReadOnly() {
            return true;
        }

        public URL getBaseUrl() {
            return appContext.getClassLoader().getResource(prefix);
        }

        public WebResource getResource(String path) {
            if (path.endsWith("/"))
                return super.getResource(path);
            URL url = appContext.getClassLoader().getResource(prefix + path);
            if (url == null) {
                return super.getResource(path);
            }
            String ext = url.toExternalForm();
            log.debug("Resource " + ext);
            if (ext.startsWith("file:")) {
                return new FileResource(root, path, new File(url.getFile()), true, null);
            }
            if (ext.startsWith("jar:")) {
                try {
                    JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                    JarEntry en = jarConnection.getJarEntry();
                    JarFile jar = jarConnection.getJarFile();
                    return new AbstractResource(root, prefix) {
                        public boolean isVirtual() {
                            return false;
                        }

                        public boolean isFile() {
                            return !en.isDirectory();
                        }

                        public boolean isDirectory() {
                            return en.isDirectory();
                        }

                        public URL getURL() {
                            return url;
                        }

                        public String getName() {
                            return en.getName();
                        }

                        public Manifest getManifest() {
                            try {
                                return jar.getManifest();
                            }
                            catch (IOException e) {
                                return null;
                            }
                        }

                        public long getLastModified() {
                            return en.getTime();
                        }

                        public long getCreation() {
                            return en.getCreationTime().toMillis();
                        }

                        public long getContentLength() {
                            return en.getSize();
                        }

                        public byte[] getContent() {
                            try {
                                return Streams.readBytes(getInputStream());
                            }
                            catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        public URL getCodeBase() {
                            return getBaseUrl();
                        }

                        public Certificate[] getCertificates() {
                            return null;
                        }

                        public String getCanonicalPath() {
                            return null;
                        }

                        public boolean exists() {
                            return true;
                        }

                        public boolean delete() {
                            return false;
                        }

                        public boolean canRead() {
                            return isFile();
                        }

                        protected org.apache.juli.logging.Log getLog() {
                            return LOG;

                        }

                        protected InputStream doGetInputStream() {
                            try {
                                return url.openStream();
                            }
                            catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
                catch (IOException e) {
                    log.debug("error when reading jar file?", e);
                }
            }
            return super.getResource(path);
        }

        @Override
        public boolean getStaticOnly() {
            return "static".equals(prefix);
        }
    }

    public String getContextPath() {
        String cp = super.getContextPath();
        return "/".equals(cp) ? "" : cp;
    }

    protected String getConfigurePrefix() {
        return PRE;
    }
    
    public Tomcat getServer() {
        return tomcat;
    }
    
    public String getMonitorName() {
        return "tomcat";
    }
}
