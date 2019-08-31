package io.nutz.demo.simple;

import javax.servlet.http.HttpSession;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {

	@Ok("raw")
	@At("/time/now")
	public long now(HttpSession session) {
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
