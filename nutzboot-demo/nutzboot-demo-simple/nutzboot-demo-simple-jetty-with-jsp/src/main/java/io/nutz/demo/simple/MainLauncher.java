package io.nutz.demo.simple;

import java.util.Date;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {
    
    @At({"/"})
    @Ok("jsp:/index")
    public NutMap index() {
        return new NutMap("date", new Date());
    }

	@Ok("raw")
	@At("/time/now")
	public long now() {
		return System.currentTimeMillis();
	}
	
	@Ok("http:500")
	@At("/error/500")
	public void error500() {
	}

	@Fail("http:500")
    @At("/error/500_2")
    public void justError() {
	    throw new RuntimeException();
    }

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
