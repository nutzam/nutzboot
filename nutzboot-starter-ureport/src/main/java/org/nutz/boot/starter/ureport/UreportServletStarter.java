package org.nutz.boot.starter.ureport;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        ServletContext sc = config.getServletContext();
        WebApplicationContext applicationContext = (WebApplicationContext) sc.getAttribute("spring.ureport");
        Object pre = sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        try {
            sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
            super.init(config);
        }
        finally {
            sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, pre);
        }
    }
}
