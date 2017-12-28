package org.nutz.boot.starter.uflo;

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

import com.bstek.uflo.console.UfloServlet;

@SuppressWarnings("serial")
@IocBean
public class UfloServletStarter extends UfloServlet implements WebServletFace {

    @PropDoc(value = "义UFLO中所有表数据主键中的缓冲区大小", defaultValue = "10")
    public static final String UFLO_IDBLOCKSIZE = "uflo.idBlockSize";

    @PropDoc(value = "是否禁用当前应用中任务提醒功能", defaultValue = "false")
    public static final String UFLO_DISABLESCHEDULER = "uflo.disableScheduler";

    @PropDoc(value = "任务提醒Job池的大小", defaultValue = "10")
    public static final String UFLO_JOBTHREADCOUNT = "uflo.jobThreadCount";

    @PropDoc(value = "是否以Daemon模式开启线程池来运行job", defaultValue = "true")
    public static final String UFLO_MAKESCHEDULERTHREADDAEMON = "uflo.makeSchedulerThreadDaemon";

    @PropDoc(value = "是否运行在debug模式下", defaultValue = "true")
    public static final String UFLO_DEBUG = "uflo.debug";

    @PropDoc(value = "用于在任务过期计算时设置一天的时长", defaultValue = "8")
    public static final String UFLO_BUSINESSDAYHOURS = "uflo.businessDayHours";

    @PropDoc(value = "设置即将过期的任务的阀值", defaultValue = "1440")
    public static final String UFLO_MINUTESBEFOREDUEDATETOREMIND = "uflo.minutesBeforeDueDateToRemind";

    @PropDoc(value = "设置即将过期的任务的阀值", defaultValue = "false")
    public static final String UFLO_DISABLEDDEPTASSIGNEEPROVIDER	 = "uflo.disabledDeptAssigneeProvider";

    @PropDoc(value = "是否禁用UFLO当中提供的默认的用于提供给流程模版设计器使用的基于用户的任务处理人分配方式。", defaultValue = "false")
    public static final String UFLO_DISABLEDUSERASSIGNEEPROVIDER		 = "uflo.disabledUserAssigneeProvider";

    @PropDoc(value = "是否禁用UFLO内部提供的默认的用于显示流程进度图任务节点消息提示功能。", defaultValue = "false")
    public static final String UFLO_DISABLEDEFAULTTASKDIAGRAMINFOPROVIDER		 = "uflo.disableDefaultTaskDiagramInfoProvider";

    @PropDoc(value = "是否禁用默认流程设计器中的基于文件夹的流程模版存储器", defaultValue = "true")
    public static final String UFLO_DISABLEDEFAULTFILEPROCESSPROVIDER	= "uflo.disableDefaultFileProcessProvider";

    @PropDoc(value = "基于文件夹的流程模版存储器采用的文件夹", defaultValue = "/WEB-INF/processfiles")
    public static final String UFLO_DEFAULTFILESTOREDIR	= "uflo.defaultFileStoreDir";

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

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        ServletContext sc = config.getServletContext();
        WebApplicationContext applicationContext = (WebApplicationContext) sc.getAttribute("spring.uflo");
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
