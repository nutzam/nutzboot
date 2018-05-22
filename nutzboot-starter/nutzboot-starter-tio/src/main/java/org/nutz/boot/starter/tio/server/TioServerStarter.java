package org.nutz.boot.starter.tio.server;

import java.nio.ByteBuffer;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslConfig;
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
public class TioServerStarter implements ServerFace {

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

    @PropDoc(group = "tio", value = "ssl keyStore路径")
    public static final String PROP_SSL_KEYSTORE_PATH = PRE + "ssl.keyStore.path";

    @PropDoc(group = "tio", value = "ssl keyStore密钥")
    public static final String PROP_SSL_KEYSTORE_PASSWORD = PRE + "ssl.keyStore.password";

    protected AioServer aioServer;

    @IocBean(name = "nopServerAioHandler")
    public NopServerAioXXX getServerAioHandler() {
        return new NopServerAioXXX();
    }

    @IocBean(name = "nopServerAioListener")
    public NopServerAioXXX getServerAioListener() {
        return new NopServerAioXXX();
    }

    @IocBean(name="serverGroupContext")
    public ServerGroupContext getServerGroupContext(@Inject ServerAioHandler serverAioHandler,
                                                    @Inject ServerAioListener serverAioListener) throws Exception {
        ServerGroupContext serverGroupContext = new ServerGroupContext(serverAioHandler, serverAioListener);
        serverGroupContext.setName(conf.get(PROP_NAME, "NutzBoot GroupContext"));
        serverGroupContext.setHeartbeatTimeout(0);
        if ("true".equals(conf.get(PROP_HEARTBEAT))) {
            serverGroupContext.setHeartbeatTimeout(conf.getLong(PROP_HEARTBEATTIMEOUT, 120000));
        }
        if (!Strings.isBlank(conf.get(PROP_SSL_KEYSTORE_PATH))) {
            SslConfig ssl = SslConfig.forServer(Files.findFileAsStream(conf.get(PROP_SSL_KEYSTORE_PATH)), null, conf.get(PROP_SSL_KEYSTORE_PASSWORD));
            serverGroupContext.setSslConfig(ssl);
        }
        return serverGroupContext;
    }

    @IocBean
    public AioServer getAioServer(@Inject ServerGroupContext serverGroupContext) {
        return new AioServer(serverGroupContext);
    }

    public void init() throws Exception {}

    public void start() throws Exception {
        log.debug("init AioServer ...");
        aioServer = appContext.getIoc().getByType(AioServer.class);
        String ip = appContext.getServerHost(PROP_IP);
        int port = appContext.getServerPort(PROP_PORT, 9420);
        aioServer.start(ip, port);
    }

    public void stop() throws Exception {
        if (aioServer != null)
            aioServer.stop();
    }

    protected static class NopServerAioXXX implements ServerAioListener, ServerAioHandler {
        public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {}

        public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {}

        public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {}

        public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {}

        public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
            return null;
        }

        public void handler(Packet packet, ChannelContext channelContext) throws Exception {}

        public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {}

        public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {}

        public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {}

        public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
            return null;
        }
    }
}
