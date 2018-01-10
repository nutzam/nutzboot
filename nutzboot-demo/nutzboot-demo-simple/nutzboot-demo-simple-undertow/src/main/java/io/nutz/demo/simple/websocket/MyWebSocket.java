package io.nutz.demo.simple.websocket;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;

@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean // 使用NutWsConfigurator的必备条件
public class MyWebSocket extends AbstractWsEndpoint {

    public WsHandler createHandler(Session session, EndpointConfig config) {
        return new MyWsHandler();
    }
}
