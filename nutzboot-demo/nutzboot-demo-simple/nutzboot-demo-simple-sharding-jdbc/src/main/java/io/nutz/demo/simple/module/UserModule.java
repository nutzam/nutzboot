package io.nutz.demo.simple.module;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import io.nutz.demo.simple.bean.UserOrder;

@At("/user")
@IocBean
public class UserModule {

    @Inject
    Dao dao;
    
    @Ok("raw")
    @At("/count")
    public long count() {
        return dao.count(UserOrder.class);
    }
    
    @Ok("json:full")
    @At("/query")
    public List<UserOrder> query(@Param("..")Pager pager) {
        return dao.query(UserOrder.class, Cnd.orderBy().asc("age"), pager);
    }
}
