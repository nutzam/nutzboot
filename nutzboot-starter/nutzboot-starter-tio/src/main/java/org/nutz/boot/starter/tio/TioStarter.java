package org.nutz.boot.starter.tio;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
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
@IocBean(create ="init")
public class TioStarter implements ServerFace {
    private static final Log log = Logs.get();

    protected static final String PRE = "tio.";

    @Inject
    protected PropertiesProxy conf;
    @PropDoc(group = "tio", value = "配置Tio监听端口", defaultValue = "9420")
    public static final String PROP_PORT = PRE + "port";
    @PropDoc(group = "tio", value = "配置ip", defaultValue = "127.0.0.1")
    public static final String PROP_IP = PRE + "host";
    @PropDoc(group = "tio", value = "主Handler类", defaultValue = "null")
    public static final String PROP_HANDLER = PRE + "handler";
    @PropDoc(group = "tio", value = "配置Listener", defaultValue = "null")
    public static final String PROP_LISTENER = PRE + "listener";
    @PropDoc(group = "tio", value = "是否启动框架层面心跳", defaultValue = "false")
    public static final String PROP_HEARTBEAT = PRE + "heartbeat";
    @PropDoc(group = "tio", value = "心跳超时时间(单位:毫秒)", defaultValue = "1000 * 120;")
    public static final String PROP_HEARTBEATTIMEOUT = PRE + "heartbeatTimeout";


    protected ServerAioHandler serverAioHandler;
    protected ServerAioListener serverAioListener = null;

    public static  ServerGroupContext serverGroupContext = null;

    public static AioServer server =null;

    private String ip = null;
    private int port;

    public void init(){
        String handler_class = conf.get(PROP_HANDLER);
        String listener_class = conf.get(PROP_LISTENER);
        if(Strings.isBlank(handler_class)){
            log.error("tio初始化失败", new Exception("未指定核心Handler类"));
        }

        try{
            serverAioHandler = (ServerAioHandler)  Mirror.me(Class.forName(handler_class)).born();;
            if(Strings.isNotBlank(listener_class)){
                serverAioListener = (ServerAioListener)  Mirror.me(Class.forName(listener_class)).born();;
            }
            serverGroupContext = new ServerGroupContext(serverAioHandler,serverAioListener);
            serverGroupContext.setName("NutzBoot GroupContext");
            serverGroupContext.setHeartbeatTimeout(0);
            String heartbeat = conf.get(PROP_HEARTBEAT);
            if(Strings.isNotBlank(heartbeat)){
                if (heartbeat.equals("true")) {
                    serverGroupContext.setHeartbeatTimeout(Long.valueOf(conf.get(PROP_HEARTBEATTIMEOUT)));
                }
            }
            ip = conf.get(PROP_IP,"127.0.0.1");
            port = Integer.parseInt(conf.get(PROP_PORT,"9420"));
            server = new AioServer(serverGroupContext);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("tio初始化失败", e);
        }
    }


    @Override
    public void start() throws Exception {
        server.start(ip,port);
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

}
