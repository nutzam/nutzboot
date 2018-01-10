package io.nutz.demo.cloud;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class EurekaServerLauncher {

    // 端口是8080
    // 首页 http://127.0.0.1:8080/eureka/jsp/status.jsp
    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
