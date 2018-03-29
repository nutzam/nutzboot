package io.nutz.cloud.demo.module;

import java.util.List;

import org.nutz.boot.starter.literpc.annotation.RpcInject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.nutz.cloud.demo.bean.User;
import io.nutz.cloud.demo.service.UserService;

@IocBean
@At("/user")
@Ok("json:full")
@AdaptBy(type=JsonAdaptor.class)
public class UserModule {
    
    private static final Log log = Logs.get();

    @RpcInject
    protected UserService userService;

    @At
    public List<User> list() {
        return userService.list();
    }

    @At
    public User fetch(@Param("id")int id) {
        return userService.fetch(id);
    }

    @POST
    public User add(@Param("name")String name, @Param("age")int age) {
        return userService.add(name, age);
    }

    @DELETE
    public void delete(@Param("id")int id) {
        userService.delete(id);
    }
    
    /**
     * 这是演示api调用的入口,会顺序调用一堆请求,请关注日志
     */
    @Ok("raw")
    @At
    public String apitest() {
        List<User> users = userService.list();
        log.info("users=" + Json.toJson(users));
        User haoqoo = userService.add("haoqoo", 19);
        User wendal = userService.add("wendal", 28);
        users = userService.list();
        log.info("users=" + Json.toJson(users));
        userService.delete(haoqoo.getId());
        userService.delete(wendal.getId());
        users = userService.list();
        log.info("users=" + Json.toJson(users));
        return "done";
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
