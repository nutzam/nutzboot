package io.nutz.demo.simple;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import org.nutz.boot.NbApp;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean(create="init")
public class MainLauncher {
	
	private static final Log log = Logs.get();
    
    @Ok("raw")
    @At("/redis/info")
    @Aop("redis")
    public String info() {
        return jedis().info();
    }
    
    @Aop("redis")
    public void init() {
        log.info(jedis().ping());
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
