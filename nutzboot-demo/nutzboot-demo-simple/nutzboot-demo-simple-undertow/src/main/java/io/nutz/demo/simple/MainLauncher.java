package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("demo")
@IocBean
public class MainLauncher {

    @ApiOperation(value = "获取当前毫秒数", notes="获取当前毫秒数", httpMethod="GET", response=Long.class)
	@Ok("raw")
	@At("/time/now")
	public long now() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
