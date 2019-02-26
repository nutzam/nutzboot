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

    @Inject
    Dao newsDao;

    @Inject
    Dao playDao;
    
    @ApiOperation(value = "获取用户总数", notes = "获取用户总数", httpMethod="GET", response=Long.class)
    @Ok("raw")
    @At
    public long count(String db) {
        // 根据入参查询不同数据库的用户数
        if(db.equals("default")) {
            return dao.count(User.class);
        } else if(db.equals("news")) {
            return newsDao.count(User.class);
        } else if(db.equals("play")) {
            return playDao.count(User.class);
        } else {
            return 0;
        }
    }
}
