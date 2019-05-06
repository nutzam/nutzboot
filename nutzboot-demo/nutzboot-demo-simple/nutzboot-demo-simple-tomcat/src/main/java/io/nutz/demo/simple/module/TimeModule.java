package io.nutz.demo.simple.module;


import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.*;
import org.nutz.mvc.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("time")
@At("/time")
@IocBean(create="init", depose="depose")
public class TimeModule {
    
    @Inject
    protected PropertiesProxy conf;
    
    @ApiOperation(value = "获取当前毫秒数", notes = "服务器端的时间", httpMethod="GET", response=Long.class)
    @At
    @Ok("raw")
    public long now() {
        return System.currentTimeMillis();
    }
    
    public void init() {}
    public void depose() {}

}
