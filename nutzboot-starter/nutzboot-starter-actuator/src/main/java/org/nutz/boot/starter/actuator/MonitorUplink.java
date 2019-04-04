package org.nutz.boot.starter.actuator;

import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.nutz.boot.NbApp;
import org.nutz.boot.starter.actuator.service.MonitorService;
import org.nutz.boot.starter.actuator.service.SimpleActuatorWebSocket;
import org.nutz.boot.tools.NbAppEventListener;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init", depose="depose")
public class MonitorUplink extends Thread implements NbAppEventListener {

    private static final Log log = Logs.get();
    
    @Inject
    protected PropertiesProxy conf;
    
    protected boolean keepRun = true;
    
    protected WebSocketClient ws;
    protected SimpleActuatorWebSocket saws;
    protected Session session;
    
    @Inject
    protected MonitorService monitorService;
    
    public MonitorUplink() throws Exception {
        setName("NutzBoot-Monitor-Uplink");
        setDaemon(true);
    }
    
    @Override
    public void afterAppStated(NbApp app) {
        start();
    }
    
    @Override
    public void run() {
        int delay = conf.getInt("actuator.uplink.delay", 5000);
        if (delay > 0)
            Lang.quiteSleep(delay);
        try {
            while (keepRun) {
                String uplinkMode = conf.get("actuator.uplink.mode", "websocket");
                int interval = 0;
                if ("http".equals(uplinkMode)) {
                    byHttp();
                    interval = conf.getInt("actuator.uplink.interval", 10*1000);
                }
                else if ("websocket".equals(uplinkMode)) {
                    byWebSocket();
                    interval = conf.getInt("actuator.uplink.interval", 1000);
                }
                if (interval < 1000)
                    interval = 10*1000;
                Thread.sleep(interval);
            }
            log.debug("system shutdown? exit");
        }
        catch (InterruptedException e) {
            log.debug("Interrupted, exit");
        }
        catch (Throwable e) {
            log.debug("something happen!!!", e);
        }
    }
    
    public void byHttp() {
        NutMap re = monitorService.getMonitors();
        String url = conf.get("actuator.uplink.url", "http://127.0.0.1:8802/monitor/uplink");
        try {
            Request req = Request.create(url, METHOD.POST);
            req.setData(Json.toJson(re, JsonFormat.full()));
            Response resp = Sender.create(req).send();
            if (resp.isOK()) {
                log.debug("monitor.uplink.success");
            }
            else {
                log.debug("monitor.uplink.fail=" + resp.getStatus());
            }
        }
        catch (Throwable e) {
            log.info("monitor.uplink.error", e);
        }
    }
    
    public void byWebSocket() {
        try {
            if (session == null || !session.isOpen()) {
                Future<Session> fu = ws.connect(saws, URI.create(conf.get("actuator.uplink.url", "ws://127.0.0.1:8802/monitor/websocket")));
                session = fu.get(5, TimeUnit.SECONDS);
            }
            NutMap re = monitorService.getMonitors();
            session.getRemote().sendString(Json.toJson(re, JsonFormat.full().setCompact(true)));
        }
        catch (Throwable e) {
            log.info("monitor.uplink.error", e);
        }
    }
    
    public void depose() throws Exception {
        keepRun = false;
        if (ws != null)
            ws.stop();
    }
    
    public void init() throws Exception {
        saws = new SimpleActuatorWebSocket();
        ws = new WebSocketClient();
        ws.start();
    }
}
