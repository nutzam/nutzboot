package org.nutz.boot.starter.jetty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.nutz.boot.AppContext;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.aware.ClassLoaderAware;
import org.nutz.boot.aware.IocAware;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.util.LifeCycle;

public class JettyStarter implements ClassLoaderAware, IocAware, ServerFace, LifeCycle, AppContextAware {
    
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
        connector.setHost(conf.get("jetty.host", "0.0.0.0"));
        connector.setPort(conf.getInt("jetty.port", 8080));
        server.setConnectors(new Connector[]{connector});
        
        
        // 设置应用上下文
        wac = new WebAppContext();
        wac.setContextPath(conf.get("jetty.contextPath", "/"));
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
        if (new File("./static").exists()) {
            resources.add(new PathResource(new File("./static")));
        }
        resources.add(Resource.newClassPathResource("static/"));
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
        
        // 设置一下额外的东西
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", conf.getInt("jetty.maxFormContentSize", 1024*1024));
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        
        // 添加其他starter提供的WebXXXX服务
        for (Object object : appContext.getStarters()) {
            if (object instanceof WebFilterFace) {
                WebFilterFace webFilter = (WebFilterFace)object;
                FilterHolder holder = new FilterHolder(webFilter.getFilter());
                holder.setName(webFilter.getName());
                holder.setInitParameters(webFilter.getInitParameters());
                wac.addFilter(holder, webFilter.getPathSpec(), webFilter.getDispatches());
            }
        }
    }

    public void fetch() throws Exception {
    }

    public void depose() throws Exception {
    }

}
