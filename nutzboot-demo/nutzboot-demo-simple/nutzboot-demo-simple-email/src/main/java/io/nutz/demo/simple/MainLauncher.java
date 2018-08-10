package io.nutz.demo.simple;

import org.apache.commons.mail.ImageHtmlEmail;
import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create = "init")
public class MainLauncher {

    private static final Log log = Logs.get();


    @Inject("refer:$ioc")
    protected Ioc ioc;

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

    public void init() {
        try {
            //ImageHtmlEmail 请不要使用注入模式，每次使用都需要去Ioc取一下
            ImageHtmlEmail htmlEmail = ioc.get(ImageHtmlEmail.class);
            htmlEmail.setSubject("NutzBootEmainStarter");
            //请使用自己的邮箱
            htmlEmail.addTo("自己的邮箱");
            htmlEmail.setHtmlMsg("此邮件是Nutz Boot Email Starter发送给您的测试邮件！");
            log.debug("开始邮件发送");
            htmlEmail.buildMimeMessage();
            htmlEmail.sendMimeMessage();
            log.debug("邮件发送完毕-请查收！注意有可能进入垃圾收件箱");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
