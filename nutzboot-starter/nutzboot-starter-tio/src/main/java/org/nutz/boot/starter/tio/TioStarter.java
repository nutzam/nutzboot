package org.nutz.boot.starter.tio;

import java.util.List;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

/**
 *
 * @Author 蛋蛋
 * @Time 2018年1月11日 19:00:01
 */
@IocBean(create = "init")
public class TioStarter implements ServerFace {
    
    private static final Log log = Logs.get();

    @Inject
    private AppContext appContext;
    
    protected static final String PRE = "tio.";

    @Inject
    protected PropertiesProxy conf;
    @PropDoc(group = "tio", value = "tio监听端口", defaultValue = "9420")
    public static final String PROP_PORT = PRE + "port";
    @PropDoc(group = "tio", value = "tio监听的ip", defaultValue = "0.0.0.0")
    public static final String PROP_IP = PRE + "host";
    @PropDoc(group = "tio", value = "是否启动框架层面心跳", defaultValue = "false")
    public static final String PROP_HEARTBEAT = PRE + "heartbeat";
    @PropDoc(group = "tio", value = "心跳超时时间(单位:毫秒)", defaultValue = "120000")
    public static final String PROP_HEARTBEATTIMEOUT = PRE + "heartbeatTimeout";
    @PropDoc(group = "tio", value = "上下文名称", defaultValue = "NutzBoot GroupContext")
    public static final String PROP_NAME = PRE + "name";

    protected ServerAioHandler serverAioHandler;
    protected ServerAioListener serverAioListener;

    public ServerGroupContext serverGroupContext;

    public AioServer server;

    private String ip;

    private int port;

    public void init() throws Exception {
        List<ServerAioHandler> hanlders = appContext.getBeans(ServerAioHandler.class);
        List<ServerAioListener> listeners = appContext.getBeans(ServerAioListener.class);

        if (hanlders.size() > 0) {
            serverAioHandler = hanlders.get(0);
        }

        if (listeners.size() > 0) {
            serverAioListener = listeners.get(0);
        }
        if (serverAioHandler == null) {
            throw new RuntimeException("Require ServerAioHandler!!!");
        }
        if (serverAioListener == null) {
            throw new RuntimeException("Require ServerAioListener!!!");
        }

        serverGroupContext = new ServerGroupContext(serverAioHandler, serverAioListener);
        serverGroupContext.setName(conf.get(PROP_NAME, "NutzBoot GroupContext"));
        serverGroupContext.setHeartbeatTimeout(0);
        if ("true".equals(conf.get(PROP_HEARTBEAT))) {
            serverGroupContext.setHeartbeatTimeout(conf.getLong(PROP_HEARTBEATTIMEOUT, 120000));
        }
        ip = appContext.getServerHost(PROP_IP);
        port = appContext.getServerPort(PROP_PORT, 9420);
        server = new AioServer(serverGroupContext);
    }

    public void start() throws Exception {
        if (server != null)
            server.start(ip, port);
        else
            log.error("tio server is null!");
    }

    public void stop() throws Exception {
        if (server != null)
            server.stop();
        else
            log.error("tio server is null!");
    }

}
