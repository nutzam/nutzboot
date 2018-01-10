package org.nutz.boot.starter.xxljob;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

@IocBean
public class XxlJobStarter implements ServerFace {

    private static final Log log = Logs.get();

    protected static final String PRE = "xxl.job.";

    @PropDoc(value = "执行器监听的ip", defaultValue = "0.0.0.0")
    public static final String PROP_EXECTOR_IP = PRE + "executor.ip";

    @PropDoc(value = "执行器监听的端口", defaultValue = "8081")
    public static final String PROP_EXECTOR_PORT = PRE + "executor.port";

    @PropDoc(value = "执行器监听的名称", defaultValue = "xxl-job-executor")
    public static final String PROP_EXECTOR_NAME = PRE + "executor.name";

    @PropDoc(value = "管理器的地址", defaultValue = "http://127.0.0.1:8080/xxl-job-admin")
    public static final String PROP_ADMIN_ADDRESSES = PRE + "admin.addresses";

    @PropDoc(value = "执行器监听的名称", defaultValue = "/var/log/xxl-job/jobhandler/")
    public static final String PROP_EXECTOR_LOGPATH = PRE + "executor.logpath";

    @PropDoc(value = "执行器的AccessToken", defaultValue = "xxl-job-executor")
    public static final String PROP_ACCESSTOKEN = PRE + "accessToken";

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @IocBean(create = "start", depose = "destroy")
    public XxlJobExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        // 声明在application.properties
        xxlJobExecutor.setIp(conf.get(PROP_EXECTOR_IP, "0.0.0.0"));
        xxlJobExecutor.setPort(conf.getInt(PROP_EXECTOR_PORT, 8081));
        xxlJobExecutor.setAppName(conf.get(PROP_EXECTOR_NAME, conf.get("nutz.application.name", "xxl-job-executor")));
        xxlJobExecutor.setAdminAddresses(conf.get(PROP_ADMIN_ADDRESSES, "http://127.0.0.1:8080/xxl-job-admin"));
        xxlJobExecutor.setLogPath(conf.get(PROP_EXECTOR_LOGPATH, "/var/log/xxl-job/jobhandler/"));
        xxlJobExecutor.setAccessToken(conf.get(PROP_ACCESSTOKEN, ""));
        return xxlJobExecutor;
    }

    public void start() throws Exception {
        // 从ioc容器中找出所有实现了IJobHandler接口的对象,注册到XxlJobExecutor
        for (IJobHandler jobHandler : appContext.getBeans(IJobHandler.class)) {
            // 看看有没有@JobHandler注解
            JobHandler annoJobHandler = jobHandler.getClass().getAnnotation(JobHandler.class);
            // 得到jobHandlerName
            String jobHandlerName = jobHandler.getClass().getSimpleName();
            if (annoJobHandler != null && !Strings.isBlank(annoJobHandler.value()))
                jobHandlerName = annoJobHandler.value();
            // 注册到XxlJobExecutor上下文
            XxlJobExecutor.registJobHandler(jobHandlerName, jobHandler);
        }
        // 获取XxlJobExecutor,从而触发XxlJobExecutor的初始化
        appContext.getIoc().getByType(XxlJobExecutor.class);
    }
}
