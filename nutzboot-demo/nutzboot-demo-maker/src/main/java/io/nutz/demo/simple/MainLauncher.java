package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@IocBean
public class MainLauncher {
    
    
    @AdaptBy(type=JsonAdaptor.class)
    @At("/maker/make")
    @Ok("raw:zip")
    public void make(@Param("..")NutMap params) {
        
    }

    public static void main(String[] args) throws Exception {
        new NbApp(MainLauncher.class).run();
    }

}
