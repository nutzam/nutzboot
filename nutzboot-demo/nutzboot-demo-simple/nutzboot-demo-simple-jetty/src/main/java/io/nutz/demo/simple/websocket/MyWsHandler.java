package io.nutz.demo.simple.websocket;

import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpSession;

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
    
    // AbstractWsEndpoint会设置session/httpSession/endpoint实例,然后调用这个init
    // 换句话说,调用init的时候, 上面的对象已经准备好了,可以直接用
    @Override
    public void init() {
        super.init();
        // HttpSession是req/resp用到的Session, 与WebSocket Session不是同一个
        if (httpSession != null) {
            // 有时候,其他service/action方法,需要往websocekt主动传输数据
            // 传输数据用到endpoint实例的send方法均需要带上websocket的id
            // 这里演示,总是把最新的websocekt session id设置到http session里面
            // 真实场景中, 可能存在一个HttpSession带N个WebSocket Session的情况
            // 这时候可以考虑用Set<String>或者数据库存储的方式解决
            httpSession.setAttribute("wsid_last", session.getId());
        }
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
