package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.boot.starter.caffeine.Cache;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {

	@Ok("raw")
	@At("/time/now")
	@Cache
	public String now() {
		return "刷新试试，我动算我输，╭(╯^╰)╮ "+System.currentTimeMillis();
	}
	
	@Ok("raw")
    @At("/time/live3s")
    @Cache("live3s")
	public String live10s() {
	    return "刷新试试，我每3秒变一下，俗称 “活不过3秒”，╭(╯^╰)╮ "+System.currentTimeMillis();
	}
	
	@Ok("raw")
	@At("/time/idel3s")
	@Cache("idel3s")
	public String idel10s() {
	    return "3秒不续命，我就玩完 ╭(╯^╰)╮ "+System.currentTimeMillis();
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
