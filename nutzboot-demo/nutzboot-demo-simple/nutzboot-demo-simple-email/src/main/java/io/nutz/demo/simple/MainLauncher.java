package io.nutz.demo.simple;

import org.apache.commons.mail.ImageHtmlEmail;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create = "init")
public class MainLauncher {

    private static final Log log = Logs.get();

    @Inject
    ImageHtmlEmail email;

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

    public void init() {
        try {
            //注意垃圾收件箱
            email.setSubject("NutzBootEmainStarter");
            //请使用自己的邮箱
            email.addTo("自己的邮箱");
            email.setHtmlMsg("此邮件是Nutz Boot Email Starter发送给您的测试邮件！");
            log.debug("开始邮件发送");
            email.send();
            log.debug("邮件发送完毕");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
