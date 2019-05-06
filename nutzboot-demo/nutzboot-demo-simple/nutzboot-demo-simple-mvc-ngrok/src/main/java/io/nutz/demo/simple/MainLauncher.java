package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {
    
    @Ok("raw")
    @At("/time/now")
    public long now() {
        System.out.println(Mvcs.getReq().getHeader("Host"));
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
    	new NbApp().setPrintProcDoc(true).run();
    }

}
