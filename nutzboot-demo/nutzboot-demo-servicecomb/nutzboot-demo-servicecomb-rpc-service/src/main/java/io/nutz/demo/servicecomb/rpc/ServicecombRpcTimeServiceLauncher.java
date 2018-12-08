package io.nutz.demo.servicecomb.rpc;

import java.net.URL;
import java.util.Enumeration;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ServicecombRpcTimeServiceLauncher {

    public static void main(String[] args) throws Exception {
        Enumeration<URL> en = ServicecombRpcTimeServiceLauncher.class.getClassLoader().getResources("META-INF/spring.schemas");
        while (en.hasMoreElements()) {
            System.out.println(en.nextElement());
        }
        new NbApp().run();
    }

}
