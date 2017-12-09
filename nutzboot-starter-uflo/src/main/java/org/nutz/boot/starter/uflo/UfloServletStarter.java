package org.nutz.boot.starter.uflo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.bstek.uflo.console.UfloServlet;

@SuppressWarnings("serial")
@IocBean
public class UfloServletStarter extends UfloServlet implements WebServletFace {

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected XmlWebApplicationContext applicationContext;

    protected ContextLoaderListener ctx;

    public String getName() {
        return "uflo";
    }

    public String getPathSpec() {
        return "/uflo/*";
    }

    public Servlet getServlet() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(config.getServletContext());
        applicationContext.setConfigLocation("classpath:uflo-spring-context.xml");
        applicationContext.refresh();
        IocContext ictx = ((Ioc2) ioc).getIocContext();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.startsWith("uflo.")) {
                switch (name) {
                case "uflo.props":
                case "uflo.dataSource":
                case "uflo.environmentProvider":
                    break;
                default:
                    Object bean = applicationContext.getBean(name);
                    ictx.save("app", name, new ObjectProxy(bean));
                    break;
                }
            }
        }
        config.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
        super.init(config);
    }

    @Override
    public void destroy() {
        applicationContext.destroy();
    }
}
