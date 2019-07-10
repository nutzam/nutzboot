package io.nutz.cloud.demo.module;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.nutz.boot.starter.literpc.annotation.RpcInject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Stopwatch;
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

    @RpcInject(endpointType="tcp")
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
    
    @Ok("raw")
    @At
    public String benchmark() {
        // 先确保service能访问
        userService.ping();
        String NL = "\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append("预热1k次").append(NL);
        Stopwatch sw = Stopwatch.begin();
        for (int i = 0; i < 1000; i++) {
            userService.ping();
        }
        sw.stop();
        sb.append("预热耗时: ").append(sw.toString()).append(NL);
        sb.append("单个请求平均耗时: ").append(sw.getDuration()/1000.0).append(NL);
        sb.append("QPS: ").append((int)(1000.0/sw.getDuration()*1000)).append(NL);
        
        
        sb.append("-----------------------").append(NL);
        sw = Stopwatch.begin();
        benchmark(10, 10000);
        sw.stop();
        sb.append("10线程执行1w次,耗时: ").append(sw.toString()).append(NL);
        sb.append("单个请求平均耗时: ").append(sw.getDuration()/10000.0).append(NL);
        sb.append("QPS: ").append((int)(10000.0/sw.getDuration()*1000)).append(NL);
        
        sb.append("-----------------------").append(NL);
        sw = Stopwatch.begin();
        benchmark(25, 10000);
        sw.stop();
        sb.append("25线程执行1w次,耗时: ").append(sw.toString()).append(NL);
        sb.append("单个请求平均耗时: ").append(sw.getDuration()/10000.0).append(NL);
        sb.append("QPS: ").append((int)(10000.0/sw.getDuration()*1000)).append(NL);
        
        sb.append("-----------------------").append(NL);
        sw = Stopwatch.begin();
        benchmark(100, 20000);
        sw.stop();
        sb.append("100线程执行1w次,耗时: ").append(sw.toString()).append(NL);
        sb.append("单个请求平均耗时: ").append(sw.getDuration()/20000.0).append(NL);
        sb.append("QPS: ").append((int)(20000.0/sw.getDuration()*1000)).append(NL);
        
        sb.append("-----------------------").append(NL);
        sw = Stopwatch.begin();
        benchmark(200, 20000);
        sw.stop();
        sb.append("200线程执行2w次,耗时: ").append(sw.toString()).append(NL);
        sb.append("单个请求平均耗时: ").append(sw.getDuration()/20000.0).append(NL);
        sb.append("QPS: ").append((int)(20000.0/sw.getDuration()*1000)).append(NL);
        
        return sb.toString();
    }
    
    public void benchmark(int client, int count) {
        ExecutorService es = Executors.newFixedThreadPool(client);
        for (int i = 0; i < count; i++) {
            es.submit(()->userService.ping());
        }
        es.shutdown();
        try {
            es.awaitTermination(5, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
