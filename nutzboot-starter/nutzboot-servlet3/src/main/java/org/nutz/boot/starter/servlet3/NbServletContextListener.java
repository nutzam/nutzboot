package org.nutz.boot.starter.servlet3;

import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

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
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class NbServletContextListener implements ServletContextListener {

    private static final Log log = Logs.get();

    @Inject
    protected AppContext appContext;

    protected NbApp _nbApp;

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

        // 注册Servlet
        appContext.getBeans(WebServletFace.class).forEach((face) -> {
            if (face.getServlet() == null) {
                return;
            }
            Dynamic dyna = sc.addServlet(face.getName(), face.getServlet());
            dyna.addMapping(face.getPathSpec());
            dyna.setInitParameters(face.getInitParameters());
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
            if (face.getFilter() == null) {
                return;
            }
            log.debugf("add filter name=%s pathSpec=%s", face.getName(), face.getPathSpec());
            javax.servlet.FilterRegistration.Dynamic dyna = sc.addFilter(face.getName(), face.getFilter());
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
    }
}
