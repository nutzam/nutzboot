package org.nutz.boot.starter.ftp;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class FtpStarter {
    private static final String PRE = "ftp.";
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

}
