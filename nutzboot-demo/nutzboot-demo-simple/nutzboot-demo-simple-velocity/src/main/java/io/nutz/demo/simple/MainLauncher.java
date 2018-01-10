package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {
    
	@At({"/", "/index"})
    @Ok("vm:/index.vm")
    public NutMap index() {
		NutMap obj = new NutMap();
		obj.setv("name", "牛牪犇").setv("age", 18);
		return obj;
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
