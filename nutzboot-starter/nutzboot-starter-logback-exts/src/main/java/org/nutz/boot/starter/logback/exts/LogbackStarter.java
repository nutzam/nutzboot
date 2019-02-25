package org.nutz.boot.starter.logback.exts;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelProperty;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.File;
import java.net.InetAddress;

@IocBean
public class LogbackStarter implements ServerFace {
    private static final Log log = Logs.get();
    private static final String PRE = "logback.exts.";
    @Inject
    protected LoglevelProperty loglevelProperty;
    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    protected PropertiesProxy conf;

    @PropDoc(value = "启用动态日志等级", defaultValue = "false", type = "boolean")
    public static final String PROP_LOGLEVEL_ENABLED = PRE + "loglevel.enabled";

    @PropDoc(value = "实例名称,不设置则自动获取nutz.application.name", type = "string")
    public static final String PROP_LOGLEVEL_NAME = PRE + "loglevel.name";

    @PropDoc(value = "心跳间隔", type = "int", defaultValue = "5")
    public static final String PROP_LOGLEVEL_HEARTBEAT = PRE + "loglevel.heartbeat";

    @PropDoc(value = "缓存时间", type = "int", defaultValue = "30")
    public static final String PROP_LOGLEVEL_KEEPALIVE = PRE + "loglevel.keepalive";

    @PropDoc(value = "启用部署监控", defaultValue = "false", type = "boolean")
    public static final String PROP_DEPLOY_ENABLED = PRE + "deploy.enabled";

    @PropDoc(value = "部署根目录", type = "string")
    public static final String PROP_DEPLOY_ROOT = PRE + "deploy.root";

    private void initLoglevelConfig() throws Exception {
        String name = conf.get(PROP_LOGLEVEL_NAME, conf.get("nutz.application.name", ""));
        if (Strings.isBlank(name)) {
            throw Lang.makeThrow("name is must!!");
        }
        loglevelProperty.setName(name);
        loglevelProperty.setEnabled(conf.getBoolean(PROP_LOGLEVEL_ENABLED, false));
        loglevelProperty.setProcessId(Lang.JdkTool.getProcessId("0"));
        loglevelProperty.setHeartbeat(conf.getInt(PROP_LOGLEVEL_HEARTBEAT, 5));
        loglevelProperty.setKeepalive(conf.getInt(PROP_LOGLEVEL_KEEPALIVE, 30));
        loglevelProperty.setUptime(Times.getTS());
        InetAddress addr = InetAddress.getLocalHost();
        loglevelProperty.setHostName(addr.getHostName());
        loglevelProperty.setHostAddress(addr.getHostAddress());
        if (conf.getBoolean(PROP_DEPLOY_ENABLED, false)) {
            //为运维中心提供版本信息支持
            String root = conf.get(PROP_DEPLOY_ROOT, "/");
            loglevelProperty.setAppVersion(getVersion(root, loglevelProperty.getName(), "app"));
            loglevelProperty.setConfVersion(getVersion(root, loglevelProperty.getName(), "conf"));
        }
    }

    public String getVersion(String root, String name, String type) {
        File f = new File(root, name + "/" + type);
        String version = "";
        if (f.exists() && f.isDirectory()) {
            File[] subDir = f.listFiles();
            for (File dir : subDir) {
                File versionFile = new File(dir.getAbsolutePath() + "/" + "version");
                if (versionFile.exists() && versionFile.isFile()) {
                    version = dir.getName();
                    break;
                }
            }
        }
        return version;
    }

    @Override
    public void start() throws Exception {
        initLoglevelConfig();
        if (loglevelProperty.isEnabled()) {
            log.debug("logback loglevel is starting...");
            ioc.get(LoglevelService.class);
        }
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public boolean isRunning() {
        return conf.getBoolean(PROP_LOGLEVEL_ENABLED, false);
    }
}
