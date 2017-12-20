package org.nutz.boot.starter.uflo;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.uflo.console.UfloServlet;

@SuppressWarnings("serial")
@IocBean
public class UfloServletStarter extends UfloServlet implements WebServletFace {
    
    public static final String URL="/uflo";

    public String getName() {
        return "uflo";
    }

    public String getPathSpec() {
        return "/uflo/*";
    }

    public Servlet getServlet() {
        return this;
    }

    protected WebApplicationContext getWebApplicationContext(ServletConfig sc) {
        return (WebApplicationContext) sc.getServletContext().getAttribute("spring.uflo");
    }

}
