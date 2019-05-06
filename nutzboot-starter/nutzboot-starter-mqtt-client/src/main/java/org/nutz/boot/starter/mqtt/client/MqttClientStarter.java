package org.nutz.boot.starter.mqtt.client;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 封装Eclipse Paho项目的Java客户端, 它实现了Mqtt 3.1.1/3.1协议, 兼容实现了该协议的服务器端,包括paho和emqtt
 * @author wendal(wendal1985@gmail.com)
 *
 */
@IocBean
public class MqttClientStarter {

    private static final Log log = Logs.get();

    /**
     * 这是客户端,暂定前缀
     */
    protected static final String PRE = "mqtt.client.";

    @PropDoc(value = "服务器地址", defaultValue = "tcp://127.0.0.1:1883")
    public static final String PROP_URL = PRE + "url";

    @PropDoc(value = "客户端id", defaultValue = "MqttClient.generateClientId()")
    public static final String PROP_CLIENT_ID = PRE + "clientId";

    @PropDoc(value = "同步客户端的最大等待时间", defaultValue = "-1")
    public static final String PROP_TIME_TO_WAIT = PRE + "timeToWait";
    
    @PropDoc(value = "启动时自动连接", defaultValue = "true")
    public static final String PROP_CONNECT_ON_START = PRE + "connectOnStart";

    @PropDoc(value = "自动重连", defaultValue = "true")
    public static final String PROP_OPTIONS_AUTOMATIC_RECONNECT = PRE + "options.automaticReconnect";

    @PropDoc(value = "心跳频率,单位秒", defaultValue = "60")
    public static final String PROP_OPTIONS_KEEP_ALIVE_INTERVAL = PRE + "options.keepAliveInterval";

    @PropDoc(value = "Will消息的topic")
    public static final String PROP_OPTIONS_WILL_TOPIC = PRE + "options.will.topic";
    @PropDoc(value = "Will消息的内容")
    public static final String PROP_OPTIONS_WILL_PAYLOAD = PRE + "options.will.payload";
    @PropDoc(value = "Will消息的QOS", defaultValue = "2")
    public static final String PROP_OPTIONS_WILL_QOS = PRE + "options.will.qos";
    @PropDoc(value = "Will消息是否retained", defaultValue = "true")
    public static final String PROP_OPTIONS_WILL_RETAINED = PRE + "options.will.retained";
    @PropDoc(value = "用户名")
    public static final String PROP_OPTIONS_USERNAME = PRE + "options.username";
    @PropDoc(value = "密码")
    public static final String PROP_OPTIONS_PASSWORD = PRE + "options.password";
    @PropDoc(value = "清除session", defaultValue = "true")
    public static final String PROP_OPTIONS_CLEAN_SESSION = PRE + "options.cleanSession";
    @PropDoc(value = "连接超时设置", defaultValue = "30")
    public static final String PROP_OPTIONS_CONNECTION_TIMEOUT = PRE + "options.connectionTimeout";
    @PropDoc(value = "多服务器地址设置")
    public static final String PROP_OPTIONS_URLS = PRE + "options.urls";
    // TODO SSL相关的配置

    @PropDoc(value = "持久化方式", defaultValue = "memory", possible = {"memory", "file"})
    public static final String PROP_PERSISTENCE_TYPE = PRE + "persistence.type";

    @PropDoc(value = "文件持久化时的目录", defaultValue = "用户主目录")
    public static final String PROP_PERSISTENCE_PATH = PRE + "persistence.path";

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;

    /**
     * MqttClient的主要配置来至于MqttConnectOptions
     */
    @IocBean(name = "mqttConnectOptions")
    public MqttConnectOptions createMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(conf.getBoolean(PROP_OPTIONS_AUTOMATIC_RECONNECT, true));
        options.setKeepAliveInterval(conf.getInt(PROP_OPTIONS_KEEP_ALIVE_INTERVAL, 60));
        if (!Strings.isBlank(conf.get(PROP_OPTIONS_WILL_TOPIC))) {
            options.setWill(conf.get(PROP_OPTIONS_WILL_TOPIC),
                            conf.get(PROP_OPTIONS_WILL_PAYLOAD).getBytes(),
                            conf.getInt(PROP_OPTIONS_WILL_QOS, 2),
                            conf.getBoolean(PROP_OPTIONS_WILL_RETAINED, true));
        }
        // 用户信息不一定存在,也不一定需要,所以需要判断一下是否真的要设置
        if (!Strings.isBlank(conf.get(PROP_OPTIONS_USERNAME))) {
            options.setUserName(conf.get(PROP_OPTIONS_USERNAME));
        }
        if (!Strings.isBlank(conf.get(PROP_OPTIONS_PASSWORD))) {
            options.setPassword(conf.get(PROP_OPTIONS_PASSWORD).toCharArray());
        }
        options.setCleanSession(conf.getBoolean(PROP_OPTIONS_CLEAN_SESSION, MqttConnectOptions.CLEAN_SESSION_DEFAULT));
        options.setConnectionTimeout(conf.getInt(PROP_OPTIONS_CONNECTION_TIMEOUT, MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT));
        // 事实上, urls的优先级是高于url的
        if (!Strings.isBlank(conf.get(PROP_OPTIONS_URLS))) {
            options.setServerURIs(Strings.splitIgnoreBlank(conf.get(PROP_OPTIONS_URLS), " "));
        }
        // TODO 完成SSL相关的配置
        return options;
    }

    /**
     * mqtt持久化策略, 默认行为是存到内存
     */
    @IocBean(name = "mqttClientPersistence")
    public MqttClientPersistence createMqttClientPersistence() {
        switch (conf.get(PROP_PERSISTENCE_TYPE, "memory")) {
        case "file":
            return new MqttDefaultFilePersistence(conf.get(PROP_PERSISTENCE_PATH, System.getProperty("user.dir")));
        default:
            return new MemoryPersistence();
        }
    }

    /**
     * 同步阻塞客户端, 然后它不支持通过MqttAsyncClient来构建,真蛋疼
     */
    @IocBean(name = "mqttClient", depose = "close")
    public MqttClient createMqttClient(@Inject MqttConnectOptions mqttConnectOptions, @Inject MqttClientPersistence mqttClientPersistence) throws MqttException {
        String clientId = conf.get(PROP_CLIENT_ID);
        if (Strings.isBlank(clientId)) {
            clientId = MqttClient.generateClientId();
        }
        log.info("Client Id = " + clientId);
        MqttClient client = new MqttClient(conf.get(PROP_URL, "tcp://127.0.0.1:1883"), clientId, mqttClientPersistence);
        if (ioc.has("mqttCallback")) {
            client.setCallback(ioc.get(MqttCallback.class, "mqttCallback"));
        }
        client.setTimeToWait(conf.getLong(PROP_TIME_TO_WAIT, -1));
        if (conf.getBoolean(PROP_CONNECT_ON_START, true)) {
            IMqttToken token = client.connectWithResult(mqttConnectOptions);
            if (token.getException() != null)
                throw token.getException();
        }
        return client;
    }

    /**
     * 异步客户端
     */
    @IocBean(name = "mqttAsyncClient", depose = "close")
    public MqttAsyncClient createMqttAsyncClient(@Inject MqttConnectOptions mqttConnectOptions, @Inject MqttClientPersistence mqttClientPersistence) throws MqttException {
        String clientId = conf.get(PROP_CLIENT_ID);
        if (Strings.isBlank(clientId)) {
            clientId = MqttClient.generateClientId();
        }
        log.info("Client Id = " + clientId);
        MqttAsyncClient client = new MqttAsyncClient(conf.get(PROP_URL, "tcp://127.0.0.1:1883"), clientId, mqttClientPersistence);
        if (ioc.has("mqttCallback")) {
            client.setCallback(ioc.get(MqttCallback.class, "mqttCallback"));
        }
        if (conf.getBoolean(PROP_CONNECT_ON_START, true)) {
            IMqttToken token = client.connect(mqttConnectOptions, null, null);
            token.waitForCompletion(conf.getLong(PROP_TIME_TO_WAIT, -1));
            if (token.getException() != null)
                throw token.getException();
        }
        return client;
    }
}
