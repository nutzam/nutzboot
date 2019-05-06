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

import io.nutz.demo.simple.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api("user")
@At("/user")
@IocBean
public class UserModule {

    @Inject
    Dao dao;
    
    @ApiOperation(value = "获取用户总数", notes = "获取用户总数", httpMethod="GET", response=Long.class)
    @Ok("raw")
    @At
    public long count() {
        return dao.count(User.class);
    }
    
    @ApiOperation(value = "查询用户列表", notes = "可分页", httpMethod="GET")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", paramType="query", value = "起始页是1", dataType="int", required = false, defaultValue="1"),
        @ApiImplicitParam(name = "pageSize", paramType="query", value = "每页数量", dataType="int", required = false, defaultValue="20"),
        })
    @Ok("json:full")
    @At
    public List<User> query(@Param("..")Pager pager) {
        return dao.query(User.class, Cnd.orderBy().asc("age"), pager);
    }
}
