package org.nutz.boot.starter.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class EmailStarter {

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

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

    @PropDoc(group = "email", value = "email的编码", defaultValue = "utf-8")
    public static final String PROP_CHARSET = PRE + "charset";

    @IocBean(name = "emailAuthenticator")
    public DefaultAuthenticator createDefaultAuthenticator() {
        return new DefaultAuthenticator(conf.get(PROP_USERNAME, ""), conf.get(PROP_PASSWORD, ""));
    }

    @IocBean(name = "htmlEmail", singleton = false)
    public ImageHtmlEmail getImageHtmlEmail() throws EmailException {
        ImageHtmlEmail imageHtmlEmail = new ImageHtmlEmail();
        imageHtmlEmail.setAuthenticator(createDefaultAuthenticator());
        imageHtmlEmail.setCharset(conf.get(PROP_CHARSET, "utf-8"));
        imageHtmlEmail.setHostName(conf.get(PROP_HOSTNAME));
        imageHtmlEmail.setSmtpPort(conf.getInt(PROP_SMTPPORT));
        imageHtmlEmail.setFrom(conf.get(PROP_FROM));
        imageHtmlEmail.setSSLOnConnect(conf.getBoolean(PROP_SSLONCONNECT));
        return imageHtmlEmail;
    }

}