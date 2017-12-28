package org.nutz.boot.starter.urule;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.urule.console.servlet.URuleServlet;

@SuppressWarnings("serial")
@IocBean
public class UruleServletStarter extends URuleServlet implements WebServletFace {

    @PropDoc(value = "配置当前资源库存放目录", defaultValue = "")
    public static final String URULE_REPOSITORY_DIR = "urule.repository.dir";

    @PropDoc(value = "在外部指定一个将资源库存储到数据库的配置文件", defaultValue = "")
    public static final String URULE_REPOSITORY_XML = "urule.repository.xml";

    @PropDoc(value = "客户端上配置的服务器地址", defaultValue = "")
    public static final String URULE_RESPORITYSERVERURL = "urule.resporityServerUrl";

    @PropDoc(value = "用来指定客户端多久到服务端检查当前知识包有没有更新", defaultValue = "0")
    public static final String URULE_KNOWLEDGEUPDATECYCLE = "urule.knowledgeUpdateCycle";

    @PropDoc(value = "一个URL，用于替换URule Console主界面第一次看到的工作区内容", defaultValue = "0")
    public static final String URULE_WELCOMEPAGE = "urule.welcomePage";

    @PropDoc(value = "一个字符串，用来替代URule控制台页面的title", defaultValue = "URule Console")
    public static final String URULE_CONSOLE_TITLE = "urule.console.title";


    public String getName() {
        return "urule";
    }

    public String getPathSpec() {
        return "/urule/*";
    }

    public Servlet getServlet() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        ServletContext sc = config.getServletContext();
        WebApplicationContext applicationContext = (WebApplicationContext) sc.getAttribute("spring.urule");
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
