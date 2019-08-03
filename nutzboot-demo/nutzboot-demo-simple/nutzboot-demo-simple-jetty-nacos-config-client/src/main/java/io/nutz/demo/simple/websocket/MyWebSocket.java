package io.nutz.demo.simple.websocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;

import io.nutz.demo.simple.service.OrderService;

@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean(create="init", depose="depose") // 使用NutWsConfigurator的必备条件
public class MyWebSocket extends AbstractWsEndpoint {
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected OrderService orderService;
    
    protected ExecutorService es;

    public WsHandler createHandler(Session session, EndpointConfig config) {
        return new MyWsHandler(ioc, orderService, es);
    }
    
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public void init() {
        es = Executors.newFixedThreadPool(16);
    }
    
    public void depose() {
        if (es != null)
            es.shutdown();
    }
}
