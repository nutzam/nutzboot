package io.nutz.demo.simple;

import javax.servlet.http.HttpSession;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {
    
    @At("/")
    @Ok("->:/index.html") // 确保websocket能拿到HttpSession, 所以主页这里主动拿一下HttpSession
    public void index(HttpSession session) {}

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
