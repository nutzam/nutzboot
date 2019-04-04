package org.nutz.boot.starter.actuator.module;

import org.nutz.boot.starter.actuator.service.MonitorService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
@Ok("json:full")
@Fail("http:500")
@At("/monitor")
public class MonitorModule {

    @Inject
    protected MonitorService monitorService;
    
    @At
    public NutMap data() {
        return monitorService.getMonitors();
    }
}
