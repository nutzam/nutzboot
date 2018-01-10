package io.nutz.demo.dubbo.rpc;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import com.alibaba.dubbo.config.annotation.Reference;

import io.nutz.demo.dubbo.rpc.service.TimeService;

@IocBean
public class DubboRpcTimeClientLauncher {
	
	@Inject
	@Reference
	protected TimeService timeService;
    
    @Ok("raw")
    @At("/time/now")
    public long now() {
        return timeService.now();
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
