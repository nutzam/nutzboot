package io.nutz.demo.cxf;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.demo.cxf.service.TimeService;

@IocBean(create = "init")
public class CxfClientLauncher {

    protected TimeService timeService;

    @Ok("raw")
    @At("/time/now")
    public long now() {
        return timeService.now();
    }

    public void init() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(TimeService.class);
        factory.setAddress("http://127.0.0.1:8081/webservice/TimeService");
        timeService = (TimeService) factory.create();
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
