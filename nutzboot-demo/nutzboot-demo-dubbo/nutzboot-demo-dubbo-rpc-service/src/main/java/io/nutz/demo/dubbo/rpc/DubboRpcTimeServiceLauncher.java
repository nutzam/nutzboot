package io.nutz.demo.dubbo.rpc;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class DubboRpcTimeServiceLauncher {

    public static void main(String[] args) throws Exception {
        new NbApp(DubboRpcTimeServiceLauncher.class).run();
    }

}
