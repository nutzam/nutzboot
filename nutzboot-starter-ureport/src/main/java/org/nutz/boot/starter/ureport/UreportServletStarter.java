package org.nutz.boot.starter.ureport;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.ureport.console.UReportServlet;

@SuppressWarnings("serial")
@IocBean
public class UreportServletStarter extends UReportServlet implements WebServletFace {
    
    public String getName() {
        return "ureport";
    }

    public String getPathSpec() {
        return "/ureport/*";
    }

    public Servlet getServlet() {
        return this;
    }

    protected WebApplicationContext getWebApplicationContext(ServletConfig sc) {
        return (WebApplicationContext) sc.getServletContext().getAttribute("spring.ureport");
    }
}
