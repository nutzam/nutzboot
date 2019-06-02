package org.nutz.boot.starter.servlet3;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

import org.nutz.boot.AppContext;
import org.nutz.boot.NbApp;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class NbServletContextListener implements ServletContextListener {

    private static final Log log = Logs.get();

    @Inject
    protected AppContext appContext;

    protected NbApp nbApp;

    protected List<ServletContextListener> listeners = new LinkedList<>();

    protected ServletContext sc;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext _sc = sce.getServletContext();
        if (_sc.getClass().getSimpleName().equals("NoPluggabilityServletContext")) {
            // fuck tomcat!!
            _sc = (ServletContext) Mirror.me(_sc).getValue(_sc, "sc");
        }
        this.sc = _sc;
        
        // 检测是不是war打包模式
        if (appContext == null && sc.getInitParameter("nutzboot.mainClass") != null) {
            String mainClassName = sc.getInitParameter("nutzboot.mainClass");
            try {
                log.info("Running at war mode!!! mainClass=" + mainClassName);
                Class<?> klass = Class.forName(mainClassName);
                Method method = null;
                try {
                    method = klass.getMethod("warMain", ServletContext.class);
                }
                catch (NoSuchMethodException e) {
                }
                if (method != null) {
                    try {
                        nbApp = (NbApp) method.invoke(null, this.sc);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    nbApp = new NbApp(klass);
                    String mainPackage = sc.getInitParameter("nutzboot.mainPackage");
                    if (Strings.isNotBlank(mainPackage))
                        nbApp.setMainPackage(mainPackage);
                }
                appContext = nbApp.getAppContext();
                nbApp.execute();
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // 注册Servlet
        appContext.getBeans(WebServletFace.class).forEach((face) -> {
            face.setServletContext(sc);
            Servlet servlet = face.getServlet();
            if (servlet == null) {
                return;
            }
            Dynamic dyna = sc.addServlet(face.getName(), servlet);
            if (dyna == null) {
                log.infof("addServlet return null?! maybe define in web.xml as same name=%s", face.getName());
                return;
            }
            for (String pathSpec : face.getPathSpecs()) {
                log.debugf("add Servlet name=%s pathSpec=%s", face.getName(), pathSpec);
                dyna.addMapping(pathSpec);
            }
            dyna.setInitParameters(face.getInitParameters());
            try {
                dyna.setAsyncSupported(face.isAsyncSupported());
                MultipartConfigElement multipartConfig = face.getMultipartConfig();
                if (multipartConfig != null)
                    dyna.setMultipartConfig(multipartConfig);
                dyna.setLoadOnStartup(face.getLoadOnStartup());
            }
            catch (Throwable e) {
            }
        });
        // 注册监听器
        appContext.getBeans(WebEventListenerFace.class).forEach((face) -> {
            EventListener en = face.getEventListener();
            if (en != null) {
                if (en instanceof ServletContextListener) {
                    listeners.add((ServletContextListener) en);
                    ((ServletContextListener) en).contextInitialized(new ServletContextEvent(sc));
                } else {
                    sc.addListener(face.getEventListener());
                }
            }
        });
        // 注册Filters
        List<WebFilterFace> filters = appContext.getBeans(WebFilterFace.class);
        Collections.sort(filters, Comparator.comparing(WebFilterFace::getOrder));
        filters.forEach((face) -> {
            face.setServletContext(sc);
            if (face.getFilter() == null) {
                return;
            }
            javax.servlet.FilterRegistration.Dynamic dyna = sc.addFilter(face.getName(), face.getFilter());
            if (dyna == null) {
                log.infof("addFilter return null?! maybe define in web.xml as same name=%s", face.getName());
                return;
            }
            log.debugf("add filter name=%s pathSpec=%s", face.getName(), face.getPathSpec());
            dyna.setInitParameters(face.getInitParameters());
            dyna.addMappingForUrlPatterns(face.getDispatches(), true, face.getPathSpec());
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (ServletContextListener listener : listeners) {
            try {
                listener.contextDestroyed(new ServletContextEvent(sc));
            }
            catch (Throwable e) {
                log.info("something happen when contextDestroyed", e);
            }
        }
        if (nbApp != null) {
            nbApp._shutdown();
        }
    }
}
