package io.nutz.demo.simple.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "demo")
@IocBean
@At("/demo")
public class SwaggerDemoModule {

    @GET
    @ApiOperation(value = "心跳接口", notes = "发我一个ping,回你一个pong", httpMethod="GET")
    @At
    @Ok("json:full")
    public Object ping() {
        return new NutMap("ok", true).setv("data", "pong");
    }

    @POST
    @ApiOperation(value = "回显接口", notes = "发我一个字符串,原样回复一个字符串", httpMethod="POST", consumes="application/x-www-form-urlencoded")
    @ApiImplicitParams({@ApiImplicitParam(name = "text", paramType="form", value = "想发啥就发啥", dataType="string", required = true)})
    @At
    @Ok("raw")
    public String echo(@Param("text") String text) {
        return text;
    }
}
