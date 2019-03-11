package org.nutz.boot.starter.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;

@IocBean
public class FtpStarter implements ServerFace {
    private static final Log log = Logs.get();
    private static final String PRE = "ftp.";
    @Inject
    protected PropertiesProxy conf;
    @PropDoc(value = "是否启动FTP客户端", defaultValue = "false", type = "boolean")
    public static final String PROP_ENABLED = PRE + "enabled";

    @PropDoc(value = "FTP地址", defaultValue = "", type = "string")
    public static final String PROP_SERVER_HOST = PRE + "host";

    @PropDoc(value = "FTP端口", defaultValue = "21", type = "int")
    public static final String PROP_SERVER_PORT = PRE + "port";

    @PropDoc(value = "FTP用户名", defaultValue = "", type = "string")
    public static final String PROP_SERVER_USERNAME = PRE + "username";

    @PropDoc(value = "FTP用户密码", defaultValue = "", type = "string")
    public static final String PROP_SERVER_PASSWORD = PRE + "password";

    @PropDoc(value = "FTP超时时间", defaultValue = "30", type = "int")
    public static final String PROP_SERVER_TIMEOUT = PRE + "timeout";

    @Inject
    private FtpService ftpService;

    @Override
    public void start() throws Exception {
        if (conf.getBoolean(PROP_ENABLED, false)) {
            ftpService.setHost(conf.get(PROP_SERVER_HOST, ""));
            ftpService.setPort(conf.getInt(PROP_SERVER_PORT, 21));
            ftpService.setUsername(conf.get(PROP_SERVER_USERNAME, ""));
            ftpService.setPassword(conf.get(PROP_SERVER_PASSWORD, ""));
            ftpService.setTimeout(conf.getInt(PROP_SERVER_TIMEOUT, 30));
            //连接一下测试是否配置正确
            FTPClient ftpClient = ftpService.connect();
            if (ftpClient != null) {
                if (ftpClient.isConnected()) {
                    try {
                        //退出登录
                        ftpClient.logout();
                        //关闭连接
                        ftpClient.disconnect();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public boolean isRunning() {
        return conf.getBoolean(PROP_ENABLED, false);
    }
}
