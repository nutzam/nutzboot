package io.nutz.demo.simple.module;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.nutz.demo.simple.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api("user")
@At("/user")
@IocBean
@Ok("json:full")
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
    @At
    public NutMap query(@Param("..")Pager pager) {
        List<User> users = dao.query(User.class, Cnd.orderBy().desc("id"), pager);
        pager.setRecordCount(dao.count(User.class));
        return new NutMap("ok", true).setv("data", new QueryResult(users, pager));
    }
    
    @At
    @POST
    public NutMap add(@Param("..")User user) {
        if (Strings.isBlank(user.getName()))
            return new NutMap("ok", false);
        dao.insert(user);
        return new NutMap("ok", true).setv("data", user);
    }
    
    @At
    @POST
    public NutMap delete(long id) {
        dao.clear(User.class, Cnd.where("id", "=", id));
        return new NutMap("ok", true);
    }
    
    @At
    @POST
    @AdaptBy(type=JsonAdaptor.class)
    public NutMap update(@Param("..")User user) {
        dao.update(user);
        return new NutMap("ok", true);
    }
}
