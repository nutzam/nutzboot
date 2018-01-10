package io.nutz.demo.cxf;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class CxfTimeServiceLauncher {

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
