package org.nutz.boot.starter.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

/**
 * @author wentao
 * @author wendal
 * @author wizzer
 * @email wentao0291@gmail.com
 * @email wendal1985@gmail.com
 * @email wizzer.cn@gmail.com
 * @date 2019-03-06 21:45
 */
@IocBean(create = "init")
public class NacosConfigureLoader {
    /**
     * 获取日志对象
     */
    private static final Log log = Logs.get();
    /**
     * Nacos配置项前缀
     */
    protected static final String NACOS_PRE = "nacos.config.";
    /**
     * Nacos远程地址配置项
     */
    @PropDoc(value = "Nacos远程地址", defaultValue = "127.0.0.1:8848")
    public static final String NACOS_ADDR = NACOS_PRE + "server-addr";
    /**
     * Nacos Data ID 配置项
     */
    @PropDoc(value = "Nacos Data ID", defaultValue = "nutzboot")
    public static final String NACOS_DATA_ID = NACOS_PRE + "data-id";
    /**
     * Nacos分组配置项
     */
    @PropDoc(value = "Nacos分组", defaultValue = "DEFAULT_GROUP")
    public static final String NACOS_GROUP = NACOS_PRE + "group";
    /**
     * Nacos数据类型配置项（用于识别使用哪种方式解析配置项） 支持配置： json, properties, xml
     */
    @PropDoc(value = "Nacos 数据类型", defaultValue = "properties")
    public static final String NACOS_DATA_TYPE = NACOS_PRE + "data-type";

    @PropDoc(value = "Nacos 编码方式", defaultValue = "")
    public static final String NACOS_ENCODE = NACOS_PRE + "encode";

    @PropDoc(value = "Nacos 命名空间ID", defaultValue = "")
    public static final String NACOS_NAMESPACE = NACOS_PRE + "namespace";

    @PropDoc(value = "Nacos AccessKey", defaultValue = "")
    public static final String NACOS_ACCESS_KEY = NACOS_PRE + "access-key";

    @PropDoc(value = "Nacos SecretKey", defaultValue = "")
    public static final String NACOS_SECRET_KEY = NACOS_PRE + "secret-key";

    @PropDoc(value = "Nacos ContextPath", defaultValue = "")
    public static final String NACOS_CONTEXT_PATH = NACOS_PRE + "context-path";

    @PropDoc(value = "Nacos 集群名称", defaultValue = "")
    public static final String NACOS_CLUSTER_NAME = NACOS_PRE + "cluster-name";

    @PropDoc(value = "Nacos 最大重试次数", defaultValue = "")
    public static final String NACOS_MAX_RETRY = NACOS_PRE + "max-retry";

    @PropDoc(value = "Nacos 配置监听长轮询超时时间", defaultValue = "")
    public static final String NACOS_CONFIG_LONG_POLL_TIMEOUT = NACOS_PRE + "config-long-poll-timeout";

    @PropDoc(value = "Nacos 配置重试时间", defaultValue = "properties")
    public static final String NACOS_CONFIG_RETRY_TIME = NACOS_PRE + "config-retry-time";

    @PropDoc(value = "Nacos 启动时拉取配置", defaultValue = "false")
    public static final String NACOS_ENABLE_REMOTE_SYNC_CONFIG = NACOS_PRE + "enable-remote-sync-config";

    @PropDoc(value = "Nacos Endpoint", defaultValue = "properties")
    public static final String NACOS_ENCODE_ENDPOINT = NACOS_PRE + "endpoint";

    @PropDoc(value = "Nacos 用户名", defaultValue = "")
    public static final String NACOS_USERNAME = NACOS_PRE + "username";

    @PropDoc(value = "Nacos 密码", defaultValue = "")
    public static final String NACOS_PASSWORD = NACOS_PRE + "password";

    @PropDoc(value = "Nacos contextPath", defaultValue = "")
    public static final String NACOS_CONTEXTPATH = NACOS_PRE + "contextPath";

    @Inject
    protected AppContext appContext;

    @Inject
    private PropertiesProxy conf;

    protected ConfigService configService;

    private List<ConfigListener> configListenerList;

    public void addConfigListener(ConfigListener configListener) {
        if (null == configListenerList) {
            configListenerList = new ArrayList<>();
        }
        configListenerList.add(configListener);
    }

    private void setConfig(String content, String contentType, PropertiesProxy conf) {
        if ("json".equals(contentType)) {
            NutMap configMap = new NutMap(content);
            conf.putAll(configMap);
        } else if ("yaml".equals(contentType)) {
            Map<String, Object> result = new HashMap<>();
            buildFlattenedMap(result, new Yaml().loadAs(content, Map.class), "");
            conf.putAll(result);
        } else if ("xml".equals(contentType)) {
            Properties properties = new Properties();
            try {
                properties.loadFromXML(new StringInputStream(content));
                for (Object key : properties.keySet()) {
                    conf.put(key.toString(), properties.get(key).toString());
                }
            } catch (IOException e) {
                throw Lang.makeThrow("nacos config xml parse error!");
            }
        } else if ("properties".equals(contentType) || "txt".equals(contentType)) {
            PropertiesProxy propertiesProxy = new PropertiesProxy(new StringInputStream(content));
            conf.putAll(propertiesProxy);
        } else {
            throw Lang.makeThrow("nacos.config.data_type is not found or not recognize，only json,xml and properties are support!");
        }
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = Strings.sNull(entry.getKey());
            if (Strings.isNotBlank(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + "." + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                StringBuilder val = new StringBuilder();
                for (Object object : collection) {
                    String str = Strings.sNull(object);
                    if (str.startsWith("{") && str.endsWith("}")) {
                        val.append(Json.toJson(object, JsonFormat.compact())).append("\n");
                    } else {
                        val.append(str).append("\n");
                    }
                }
                result.put(key, val.toString());
            } else {
                result.put(key, Strings.sNull(value));
            }
        }
    }

    protected String dataId;
    protected String group;

    public void init() throws Exception {
        dataId = conf.get(NACOS_DATA_ID, conf.get("nutz.application.name", "nutzboot"));
        group = conf.get(NACOS_GROUP, "DEFAULT_GROUP");
        configService = NacosFactory.createConfigService(getNacosConfigProperties());
        String configInfo = configService.getConfigAndSignListener(dataId, group, 5000, new com.alibaba.nacos.api.config.listener.Listener() {

            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                updateConfigString(configInfo);
                if (null != configListenerList) {
                    for (ConfigListener configListener : configListenerList) {
                        configListener.reloadConfig();
                    }
                }
            }
        });
        updateConfigString(configInfo);
    }

    protected void updateConfigString(String configInfo) {
        log.debugf("get nacos config：%s", configInfo);
        String dataType = conf.get(NACOS_DATA_TYPE, "properties");
        if (Strings.isNotBlank(configInfo)) {
            setConfig(configInfo, dataType, conf);
        }
    }

    public Properties getNacosConfigProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, conf.get(NACOS_ADDR, "127.0.0.1:8848"));
        properties.put(ENCODE, conf.get(NACOS_ENCODE, ""));
        properties.put(NAMESPACE, conf.get(NACOS_NAMESPACE, ""));
        properties.put(ACCESS_KEY, conf.get(NACOS_ACCESS_KEY, ""));
        properties.put(SECRET_KEY, conf.get(NACOS_SECRET_KEY, ""));
        properties.put(CONTEXT_PATH, conf.get(NACOS_CONTEXT_PATH, conf.get(NACOS_CONTEXTPATH, "/nacos")));
        properties.put(CLUSTER_NAME, conf.get(NACOS_CLUSTER_NAME, ""));
        properties.put(MAX_RETRY, conf.get(NACOS_MAX_RETRY, ""));
        properties.put(CONFIG_LONG_POLL_TIMEOUT, conf.get(NACOS_CONFIG_LONG_POLL_TIMEOUT, ""));
        properties.put(CONFIG_RETRY_TIME, conf.get(NACOS_CONFIG_RETRY_TIME, ""));
        properties.put(ENABLE_REMOTE_SYNC_CONFIG, conf.get(NACOS_ENABLE_REMOTE_SYNC_CONFIG, ""));
        properties.put(USERNAME, conf.get(NACOS_USERNAME, ""));
        properties.put(PASSWORD, conf.get(NACOS_PASSWORD, ""));
        String endpoint = conf.get(NACOS_ENCODE_ENDPOINT, "");
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }
        return properties;
    }

    @IocBean(name = "nacosConfigService")
    public ConfigService getConfigService() {
        return configService;
    }

    public String getDataId() {
        return dataId;
    }

    public String getGroup() {
        return group;
    }
}
