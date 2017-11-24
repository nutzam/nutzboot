package io.nutz.demo.simple;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {
    
    @Ok("raw")
    @At("/time/now")
    public long now() {
        return System.currentTimeMillis();
    }
    
    @Ok("raw")
    @At("/shiro/test")
    public boolean isAuthenticated() {
    	Subject subject = SecurityUtils.getSubject();
    	return subject.isAuthenticated();
    }

    public static void main(String[] args) throws Exception {
    	new NbApp(MainLauncher.class).setPrintProcDoc(true).run();
    }

}
