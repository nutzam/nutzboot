package io.nutz.demo.simple.module;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mongo.ZMoDB;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@At("/user")
@IocBean
public class UserModule {

    @Inject
    ZMoDB zmodb;
    
    @Ok("raw")
    @At("/count")
    public long count() {
        return zmodb.cc("user", false).count();
    }
    
    @Ok("json:full")
    @At("/query")
    public Object query(@Param("..")Pager pager) {
    	return zmodb.cc("user", false).find().limit(pager.getPageSize());
    }
}
