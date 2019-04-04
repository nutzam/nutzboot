package io.nutz.demo.servicecomb.rpc;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.demo.servicecomb.rpc.service.TimeService;

@IocBean
public class ServiceCombRpcTimeClientLauncher {
	
    @RpcReference(microserviceName = "rpcDemo", schemaId = "time")
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
