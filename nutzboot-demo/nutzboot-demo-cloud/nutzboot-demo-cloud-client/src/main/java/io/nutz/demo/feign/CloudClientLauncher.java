package io.nutz.demo.feign;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class CloudClientLauncher {

    // 端口是8082
    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
