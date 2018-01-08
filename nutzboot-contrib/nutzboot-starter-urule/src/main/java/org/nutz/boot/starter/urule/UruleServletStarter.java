package org.nutz.boot.starter.urule;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.WebApplicationContext;

import com.bstek.urule.console.servlet.URuleServlet;

@SuppressWarnings("serial")
@IocBean
public class UruleServletStarter extends URuleServlet implements WebServletFace {

    @PropDoc(value = "配置当前资源库存放目录", need=true)
    public static final String PROP_REPOSITORY_DIR = "urule.repository.dir";

    @PropDoc(value = "在外部指定一个将资源库存储到数据库的配置文件")
    public static final String PROP_REPOSITORY_XML = "urule.repository.xml";

    @PropDoc(value = "客户端上配置的服务器地址")
    public static final String PROP_RESPORITYSERVERURL = "urule.resporityServerUrl";

    @PropDoc(value = "用来指定客户端多久到服务端检查当前知识包有没有更新", defaultValue = "0")
    public static final String PROP_KNOWLEDGEUPDATECYCLE = "urule.knowledgeUpdateCycle";

    @PropDoc(value = "一个URL，用于替换URule Console主界面第一次看到的工作区内容")
    public static final String PROP_WELCOMEPAGE = "urule.welcomePage";

    @PropDoc(value = "一个字符串，用来替代URule控制台页面的title", defaultValue = "URule Console")
    public static final String PROP_CONSOLE_TITLE = "urule.console.title";


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
