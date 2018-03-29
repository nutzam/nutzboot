package org.nutz.boot.starter.loach.client;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class LoachClientStarter implements ServerFace {

    @PropDoc(value = "是否启用'泥鳅'客户端", defaultValue = "true")
    public static final String PROP_CLIENT_ENABLE = "loach.client.enable";
    
    @PropDoc(value = "是否启用'泥鳅'服务器列表更新", defaultValue = "true")
    public static final String PROP_UPDATER_ENABLE = "loach.updater.enable";

    @PropDoc(value = "对外服务的虚拟ip或者域名", defaultValue="127.0.0.1")
    public static final String PROP_HOST = "server.vip";

    @PropDoc(value = "心跳频率，单位毫秒", defaultValue = "3000", type = "int")
    public static final String PROP_PING_INTERVAL = "loach.client.ping.interval";

    @PropDoc(value = "启动延时，单位毫秒", defaultValue = "-1", type = "int")
    public static final String PROP_STARTUP_DELAY = "loach.client.startUpDelay";

    @PropDoc(value = "'泥鳅'服务器的URL,可以多个,用分号隔开即可", defaultValue = "http://127.0.0.1:8610/loach/v1")
    public static final String PROP_URLS = "loach.client.urls";

    @PropDoc(value = "调试模式", defaultValue = "false")
    public static final String PROP_DEBUG = "loach.client.debug";


    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void start() throws Exception {
        ioc.get(LoachClient.class);
    }
}
