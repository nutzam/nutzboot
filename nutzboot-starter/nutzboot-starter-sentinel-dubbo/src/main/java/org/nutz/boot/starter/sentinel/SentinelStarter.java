package org.nutz.boot.starter.sentinel;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Random;
import java.util.Set;

@IocBean
public class SentinelStarter implements ServerFace {
    private static final Log log = Logs.get();
    private static final String PRE = "sentinel.";
    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    protected PropertiesProxy conf;
    private PubSubService pubSubService;
    private RedisService redisService;

    private static final String CONSOLE_SERVER = "csp.sentinel.dashboard.server";
    private static final String SERVER_PORT = "csp.sentinel.api.port";
    private static final String HEARTBEAT_INTERVAL_MS = "csp.sentinel.heartbeat.interval.ms";
    private static final String HEARTBEAT_CLIENT_IP = "csp.sentinel.heartbeat.client.ip";

    @PropDoc(value = "是否启动Sentinel客户端", defaultValue = "false", type = "boolean")
    public static final String PROP_ENABLED = PRE + "enabled";

    @PropDoc(value = "Sentinel客户端名称,不设置则自动获取nutz.application.name", type = "string")
    public static final String PROP_PROJECR_NAME = PRE + "project.name";

    @PropDoc(value = "Sentinel客户端端口", defaultValue = "8721", type = "int")
    public static final String PROP_SERVER_PORT = PRE + SERVER_PORT;

    @PropDoc(value = "Sentinel控制台地址", defaultValue = "localhost:9090", type = "string")
    public static final String PROP_CONSOLE_SERVER = PRE + CONSOLE_SERVER;

    @PropDoc(value = "Sentinel客户端通信间隔毫秒数", defaultValue = "3000", type = "int")
    public static final String PROP_HEARTBEAT_INTERVAL_MS = PRE + HEARTBEAT_INTERVAL_MS;

    @PropDoc(value = "Sentinel客户端IP,不配置则自动获取本地IP", defaultValue = "", type = "string")
    public static final String PROP_HEARTBEAT_CLIENT_IP = PRE + HEARTBEAT_CLIENT_IP;

    @PropDoc(value = "Sentinel规则存储key值", defaultValue = "nutzboot", type = "string")
    public static final String PROP_RULEKEY = PRE + "rulekey";

    @PropDoc(value = "Sentinel规则存储channel值", defaultValue = "sentinel", type = "string")
    public static final String PROP_CHANNEL = PRE + "channel";

    @Override
    public void start() throws Exception {
        if (conf.getBoolean(PROP_ENABLED, false)) {
            redisService = ioc.get(RedisService.class);
            pubSubService = ioc.get(PubSubService.class);
            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("project.name", conf.get(PROP_PROJECR_NAME, conf.get("nutz.application.name", conf.get("dubbo.application.name", ""))));
            System.setProperty(CONSOLE_SERVER, conf.get(PROP_CONSOLE_SERVER, "localhost:9090"));
            System.setProperty(HEARTBEAT_INTERVAL_MS, conf.get(PROP_HEARTBEAT_INTERVAL_MS, "3000"));
            String ip = conf.get(PROP_HEARTBEAT_CLIENT_IP, "");
            int port = conf.getInt(PROP_SERVER_PORT, 0);
            if (port == 0) {
                port = getRandPort(ip);
            }
            System.setProperty(HEARTBEAT_CLIENT_IP, ip);
            System.setProperty(SERVER_PORT, "" + port);
            registerFlowRuleSupplier();
            registerSystemRuleSupplier();
            registerAuthorityRuleSupplier();
            registerDegradeRuleSupplier();
            registerClusterRuleSupplier();
            log.infof("sentinel started in %s:%s", TransportConfig.getHeartbeatClientIp(), TransportConfig.getPort());
        }
    }

    private void registerFlowRuleSupplier() {
        SentinelReadableDataSource<List<FlowRule>> redisDataSource =
                new SentinelReadableDataSource<>(
                        redisService,
                        pubSubService,
                        conf.get(PROP_RULEKEY, "nutzboot") + "-flow-rule",
                        conf.get(PROP_CHANNEL, "sentinel") + "-flow-rule",
                        source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                        }));
        FlowRuleManager.register2Property(redisDataSource.getProperty());
        WritableDataSource<List<FlowRule>> wds = new SentinelWritableDataSource<>(redisService, this::encodeJson, conf.get(PROP_RULEKEY, "nutzboot") + "-flow-rule");
        // Register to writable data source registry so that rules can be updated to redis
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerFlowDataSource(wds);
    }

    private void registerSystemRuleSupplier() {
        SentinelReadableDataSource<List<SystemRule>> redisDataSource =
                new SentinelReadableDataSource<>(
                        redisService,
                        pubSubService,
                        conf.get(PROP_RULEKEY, "nutzboot") + "-system-rule",
                        conf.get(PROP_CHANNEL, "sentinel") + "-system-rule",
                        source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
                        }));
        SystemRuleManager.register2Property(redisDataSource.getProperty());
        WritableDataSource<List<SystemRule>> wds = new SentinelWritableDataSource<>(redisService, this::encodeJson, conf.get(PROP_RULEKEY, "nutzboot") + "-system-rule");
        // Register to writable data source registry so that rules can be updated to redis
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerSystemDataSource(wds);
    }

    private void registerAuthorityRuleSupplier() {
        SentinelReadableDataSource<List<AuthorityRule>> redisDataSource =
                new SentinelReadableDataSource<>(
                        redisService,
                        pubSubService,
                        conf.get(PROP_RULEKEY, "nutzboot") + "-authority-rule",
                        conf.get(PROP_CHANNEL, "sentinel") + "-authority-rule",
                        source -> JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {
                        }));
        AuthorityRuleManager.register2Property(redisDataSource.getProperty());
        WritableDataSource<List<AuthorityRule>> wds = new SentinelWritableDataSource<>(redisService, this::encodeJson, conf.get(PROP_RULEKEY, "nutzboot") + "-authority-rule");
        // Register to writable data source registry so that rules can be updated to redis
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerAuthorityDataSource(wds);
    }

    private void registerDegradeRuleSupplier() {
        SentinelReadableDataSource<List<DegradeRule>> redisDataSource =
                new SentinelReadableDataSource<>(
                        redisService,
                        pubSubService,
                        conf.get(PROP_RULEKEY, "nutzboot") + "-degrade-rule",
                        conf.get(PROP_CHANNEL, "sentinel") + "-degrade-rule",
                        source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                        }));
        DegradeRuleManager.register2Property(redisDataSource.getProperty());
        WritableDataSource<List<DegradeRule>> wds = new SentinelWritableDataSource<>(redisService, this::encodeJson, conf.get(PROP_RULEKEY, "nutzboot") + "-degrade-rule");
        // Register to writable data source registry so that rules can be updated to redis
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerDegradeDataSource(wds);
    }

    private void registerClusterRuleSupplier() {
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            SentinelReadableDataSource<List<FlowRule>> ds = new SentinelReadableDataSource<>(
                    redisService,
                    pubSubService,
                    conf.get(PROP_RULEKEY, "nutzboot") + namespace + "-cluster-flow-rule",
                    conf.get(PROP_CHANNEL, "sentinel") + namespace + "-cluster-flow-rule",
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                    }));
            return ds.getProperty();
        });
        // Register cluster parameter flow rule property supplier.
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            SentinelReadableDataSource<List<ParamFlowRule>> ds = new SentinelReadableDataSource<>(
                    redisService,
                    pubSubService,
                    conf.get(PROP_RULEKEY, "nutzboot") + namespace + "-param-flow-rule",
                    conf.get(PROP_CHANNEL, "sentinel") + namespace + "-param-flow-rule",
                    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
                    }));
            return ds.getProperty();
        });
        // Server namespace set (scope) data source.
        SentinelReadableDataSource<Set<String>> namespaceDs = new SentinelReadableDataSource<>(redisService,
                pubSubService,
                conf.get(PROP_RULEKEY, "nutzboot") + "-namespace",
                conf.get(PROP_CHANNEL, "sentinel") + "-namespace",
                source -> JSON.parseObject(source, new TypeReference<Set<String>>() {
                }));
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
        // Server transport configuration data source.
        SentinelReadableDataSource<ServerTransportConfig> transportConfigDs = new SentinelReadableDataSource<>(redisService,
                pubSubService,
                conf.get(PROP_RULEKEY, "nutzboot") + "-transport",
                conf.get(PROP_CHANNEL, "sentinel") + "-transport",
                source -> JSON.parseObject(source, new TypeReference<ServerTransportConfig>() {
                }));
        ClusterServerConfigManager.registerServerTransportProperty(transportConfigDs.getProperty());
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }

    private int getRandPort(String host) {
        int port = 20000 + new Random(System.currentTimeMillis()).nextInt(2000);
        if (isPortUsing(host, port)) {
            return getRandPort(host);
        }
        return port;
    }

    private boolean isPortUsing(String ip, int port) {
        boolean flag = false;
        try {
            Socket socket = new Socket();
            socket.setReceiveBufferSize(8192);
            socket.setSoTimeout(1000);
            SocketAddress address = new InetSocketAddress(ip, port);
            socket.connect(address, 1000);// 判断ip、端口是否可连接
            if (socket.isConnected()) {
                flag = true;
                socket.close();
            }
        } catch (IOException e) {
        }
        return flag;
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public boolean isRunning() {
        return conf.getBoolean(PROP_ENABLED, false);
    }
}
