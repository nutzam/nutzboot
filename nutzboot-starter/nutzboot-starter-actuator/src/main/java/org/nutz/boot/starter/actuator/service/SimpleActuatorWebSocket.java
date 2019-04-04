package org.nutz.boot.starter.actuator.service;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@WebSocket(maxTextMessageSize = 128 * 1024)
public class SimpleActuatorWebSocket {
    
    private static final Log log = Logs.get();

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        if (log.isDebugEnabled())
            log.debug("got message: " + msg);
    }
}