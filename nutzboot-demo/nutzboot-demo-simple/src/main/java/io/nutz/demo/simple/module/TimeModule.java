package io.nutz.demo.simple.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.At;

@IocBean
@At("/time")
public class TimeModule {

    @Ok("raw")
    @At
    public long now() {
        return System.currentTimeMillis();
    }
}
