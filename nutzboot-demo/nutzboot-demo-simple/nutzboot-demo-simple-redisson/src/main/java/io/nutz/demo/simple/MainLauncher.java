package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

@IocBean(create="init")
public class MainLauncher {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected RedissonClient redissonClient;

	// 通过注入获取
    @Inject("java:$redissonClient.getMap('rmap')")
	protected RMap<Object, Object> rmap;
	
    // 手动赋值
	protected RMap<Object, Object> rmap2;
    
    @Ok("json:full")
    @At("/redis/info")
    public NutMap info() {
        NutMap map = new NutMap();
        map.put("size", rmap.size());
        map.put("name", rmap.get("name"));
        map.put("age", rmap.get("age"));
        return map;
    }

    public void init() {
        log.info("loading RMap from redisson");
        rmap2 = redissonClient.getMap("myrmap2");
        rmap.put("name", "wendal");
        rmap.put("age", 123);
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
