package io.nutz.demo.zbus.rpc;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.demo.zbus.rpc.service.TimeService;

@IocBean
public class ZbusRpcTimeClientLauncher {
	
	@Inject
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
