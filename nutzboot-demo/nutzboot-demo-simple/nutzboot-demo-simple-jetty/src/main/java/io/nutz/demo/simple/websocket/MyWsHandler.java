package io.nutz.demo.simple.websocket;

import java.util.concurrent.ExecutorService;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.handler.SimpleWsHandler;

import io.nutz.demo.simple.service.OrderService;

public class MyWsHandler extends SimpleWsHandler {

    protected Ioc ioc;
    protected OrderService orderService;
    protected ExecutorService es;
    
    public MyWsHandler(Ioc ioc, OrderService orderService, ExecutorService es) {
        super();
        this.ioc = ioc;
        this.orderService = orderService;
        this.es = es;
    }
    
    
    // 对应js代码 ws.send('{action:"buy", order:1}')
    public void buy(NutMap params) {
        // 先检查一下参数
        Number n = params.getAs("order", Number.class);
        if (n == null) {
            // 没提过数量就拒绝咯
            endpoint.sendJson(session.getId(), new NutMap("ok", false).setv("msg", "没有提供数量"));
        }
        else {
            // 返回处理结果
            boolean re = orderService.buy(n.intValue());
            endpoint.sendJson(session.getId(), new NutMap("ok", re));
        }
    }
}
