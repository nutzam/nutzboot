package io.nutz.demo.simple;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

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
        return null;
    }

    @At
    @Ok("raw")
    public Object get(@Param("region") String region){
        cacheChannel.clear(region);
        return true;
    }


    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
