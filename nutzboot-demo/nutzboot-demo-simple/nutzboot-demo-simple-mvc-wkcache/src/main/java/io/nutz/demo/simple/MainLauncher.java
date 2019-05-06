package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.wkcache.annotation.CacheResult;

@IocBean
public class MainLauncher {
    
    private static final Log log = Logs.get();

	@Ok("raw")
	@At("/time/now")
	@CacheResult(cacheName="demo")
	public long now() {
	    log.info("hi, i am here", new Throwable());
		return System.currentTimeMillis();
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
