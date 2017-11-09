package org.nutz.start.swagger;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class SwaggerStarter implements WebServletFace {
    
    @Inject
    protected PropertiesProxy conf;

    public String getName() {
        return "swagger";
    }

    public String getPathSpec() {
        return "/swagger/*";
    }

    public Servlet getServlet() {
        return new SwaggerServlet();
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

}
