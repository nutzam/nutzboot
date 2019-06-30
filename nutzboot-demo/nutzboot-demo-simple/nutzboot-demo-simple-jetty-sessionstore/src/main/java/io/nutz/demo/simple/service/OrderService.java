package io.nutz.demo.simple.service;

import java.util.concurrent.atomic.AtomicLong;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class OrderService {
    
    private static final Log log = Logs.get();

    // 假装有库存
    protected AtomicLong store = new AtomicLong(100);
    
    public boolean buy(int number) {
        // 购买数量不合法,直接返还false咯
        if (number < 1)
            return false;
        // 减少库存
        long after = store.addAndGet(0 - number);
        if (after >= 0) {
            // 减少之后依然有剩余,那就购买成功咯
            log.info("购买成功");
            return true;
        }
        else {
            // 减少之后库存是负数了,购买失败咯,返还库存
            store.addAndGet(number);
            log.info("购买失败");
            return false;
        }
    }
}
