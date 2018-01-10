package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {

    @At({"/", "/index"})
    @Ok("th:/index.html")
    public NutMap index() {
        return NutMap.NEW().setv("name", "NB").setv("age", 18);
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
