package org.nutz.boot.tools;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.integration.spring.SpringIocLoader2;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.springframework.web.context.support.XmlWebApplicationContext;

public abstract class SpringWebContextProxy implements ServletContextListener, WebEventListenerFace {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected AppContext appContext;

    protected XmlWebApplicationContext applicationContext;

    protected String configLocation;
    protected String selfName;

    public void contextInitialized(ServletContextEvent sce) {
        applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(sce.getServletContext());
        applicationContext.setConfigLocation(configLocation);
        applicationContext.refresh();
        appContext.getComboIocLoader().addLoader(new SpringIocLoader2(applicationContext, getSpringBeanNames().toArray(new String[0])));
        sce.getServletContext().setAttribute("spring." + selfName, applicationContext);
    }

    protected List<String> getSpringBeanNames() {
        List<String> names = new ArrayList<>();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.startsWith(selfName + ".")) {
                names.add(name);
            }
        }
        return names;
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (applicationContext != null)
            applicationContext.destroy();
    }

    public EventListener getEventListener() {
        return this;
    }
}
