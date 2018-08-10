package org.nutz.boot.starter.email;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.ioc.IocLoader;

public class EmailIocLoaderStarter implements IocLoaderProvider {

    protected static final String PRE = "email.";

    @PropDoc(group = "email", value = "email的ip或域名地址", need = true)
    public static final String PROP_HOSTNAME = PRE + "HostName";

    @PropDoc(group = "email", value = "email的SmtpPort端口", need = true)
    public static final String PROP_SMTPPORT = PRE + "SmtpPort";

    @PropDoc(group = "email", value = "email的用户名", need = true)
    public static final String PROP_USERNAME = PRE + "UserName";

    @PropDoc(group = "email", value = "email的密码", need = true)
    public static final String PROP_PASSWORD = PRE + "Password";

    @PropDoc(group = "email", value = "email开启SSL连接", defaultValue = "true", type = "boolean")
    public static final String PROP_SSLONCONNECT = PRE + "SSLOnConnect";

    @PropDoc(group = "email", value = "email的写信人", need = true)
    public static final String PROP_FROM = PRE + "From";

    @PropDoc(group = "email", value = "email的编码", defaultValue = "UTF-8")
    public static final String PROP_CHARSET = PRE + "charset";

    @Override
    public IocLoader getIocLoader() {
        return new EmailIocLoader();
    }
}