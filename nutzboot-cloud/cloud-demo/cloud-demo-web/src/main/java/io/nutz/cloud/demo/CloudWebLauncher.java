package io.nutz.cloud.demo;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class CloudWebLauncher {

    // 端口是8082, 请先启动 loach-server
    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
