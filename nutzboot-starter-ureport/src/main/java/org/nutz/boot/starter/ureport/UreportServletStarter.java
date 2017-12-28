package org.nutz.boot.starter.ureport;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.ureport.console.UReportServlet;

@SuppressWarnings("serial")
@IocBean
public class UreportServletStarter extends UReportServlet implements WebServletFace {

    @PropDoc(value = "定义UReport2中提供的默认基于文件系统的报表存储目录", defaultValue = "")
    public static final String UREPORT_FILESTOREDIR = "ureport.fileStoreDir";


    @PropDoc(value = "是否启用自定义报表存储器", defaultValue = "")
    public static final String UREPORT_DISABLEFILEPROVIDER= "ureport.disableFileProvider";


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
