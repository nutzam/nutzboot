package org.nutz.boot.starter;

import org.nutz.boot.NbApp;
import org.nutz.boot.starter.feignclient.UserFeignClient;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import java.util.Map;

@IocBean
public class MainLauncher {


    @Inject("java:$feignRegister.get('UserFeignClient')")
    private UserFeignClient userFeignClient;

    @At({"/", "/index"})
    @Ok("json")
    public NutMap index() {
        NutMap obj = new NutMap();
        obj.setv("name", "牛牪犇").setv("age", 18);
        return obj;
    }


    @At({"/test"})
    @Ok("json")
    public Map test() {
        Map u =  userFeignClient.index();
        return userFeignClient.index();
    }


    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();

        System.out.println(1234);
    }

}
