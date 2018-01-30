package io.nutz.demo.simple;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * J2CacheStarter的Demo演示
 * @Author 蛋蛋的忧伤(https://github.com/TopCoderMyDream)
 * @Time 2018年1月28日 10:04:52
 */
@IocBean()
public class MainLauncher {

    @Inject
    protected CacheChannel cacheChannel;

    @At
    @Ok("raw")
    public String put(@Param("region") String region,@Param("name") String name,@Param("val") String val){
        cacheChannel.set(region,name,val);
        return "success";
    }

    @At
    @Ok("raw")
    public Object get(@Param("region") String region, @Param("name") String name){
        CacheObject cacheObject = cacheChannel.get(region, name);
        if(cacheObject!= null){
            return  cacheObject.getValue();
        }
        return "空";
    }

    @At
    @Ok("raw")
    public Object evict(@Param("region")String region,@Param("name")String name){
       cacheChannel.evict(region, name);
       return "ok";
    }

    @At
    @Ok("raw")
    public Object clear(@Param("region") String region){
        cacheChannel.clear(region);
        return true;
    }


    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
