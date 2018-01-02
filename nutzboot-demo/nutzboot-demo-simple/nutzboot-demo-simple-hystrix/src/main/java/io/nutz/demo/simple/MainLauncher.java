package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("time")
@IocBean(create = "init")
public class MainLauncher {
	
    @ApiOperation(value = "获取当前毫秒数", notes="获取当前毫秒数", httpMethod="GET", response=Long.class)
    @HystrixCommand
	@At("/time/now")
    @Ok("raw")
	public long now() {
	    return System.currentTimeMillis();
	}
    
    public void init() {
        now();
    }

	public static void main(String[] args) {
		new NbApp().setPrintProcDoc(true).run();
	}
}
