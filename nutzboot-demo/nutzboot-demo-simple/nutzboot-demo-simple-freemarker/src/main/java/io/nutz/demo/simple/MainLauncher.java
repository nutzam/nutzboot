package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("")
public class MainLauncher {

    @At("/")
    @Ok("fm:/index")
    public Object index(){
        return NutMap.NEW().setv("name","wendal").setv("age",18);
    }


    public static void main(String[] args) {
        new NbApp().setPrintProcDoc(true).start();
    }
}
