package org.nutz.boot.starter.urule;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.urule.console.servlet.URuleServlet;

@SuppressWarnings("serial")
@IocBean
public class UruleServletStarter extends URuleServlet implements WebServletFace {
    
    public String getName() {
        return "urule";
    }

    public String getPathSpec() {
        return "/urule/*";
    }

    public Servlet getServlet() {
        return this;
    }

    protected WebApplicationContext getWebApplicationContext(ServletConfig sc) {
        return (WebApplicationContext) sc.getServletContext().getAttribute("spring.urule");
    }
}
