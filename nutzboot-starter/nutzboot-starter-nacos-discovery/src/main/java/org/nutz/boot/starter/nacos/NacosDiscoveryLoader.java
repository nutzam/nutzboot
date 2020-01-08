package org.nutz.boot.starter.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.config.impl.AbstractConfigureLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Properties;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/8
 */
@IocBean
public class NacosDiscoveryLoader extends AbstractConfigureLoader {
    /**
     * 获取日志对象
     */
    private static final Log log = Logs.get();
    /**
     * Nacos配置项前缀
     */
    protected static final String NACOS_PRE = "nacos.discovery.";
    /**
     * Nacos远程地址配置项
     */
    @PropDoc(value = "Nacos 远程地址", defaultValue = "127.0.0.1:8848")
    public static final String NACOS_ADDR = NACOS_PRE + "server-addr";

    @PropDoc(value = "Nacos 命名空间ID", defaultValue = "")
    public static final String NACOS_NAMESPACE = NACOS_PRE + "namespace";

    @PropDoc(value = "Nacos 日志文件名", defaultValue = "")
    public static final String NACOS_LOG_FILENAME = NACOS_PRE + "log-filename";

    @PropDoc(value = "Nacos 日志等级", defaultValue = "")
    public static final String NACOS_LOG_LEVEL = NACOS_PRE + "log-level";

    @PropDoc(value = "Nacos Endpoint", defaultValue = "")
    public static final String NACOS_ENCODE_ENDPOINT = NACOS_PRE + "endpoint";

    @PropDoc(value = "Nacos AccessKey", defaultValue = "")
    public static final String NACOS_ACCESS_KEY = NACOS_PRE + "access-key";

    @PropDoc(value = "Nacos SecretKey", defaultValue = "")
    public static final String NACOS_SECRET_KEY = NACOS_PRE + "secret-key";

    @PropDoc(value = "Nacos 集群名称", defaultValue = "")
    public static final String NACOS_CLUSTER_NAME = NACOS_PRE + "cluster-name";

    @PropDoc(value = "Nacos 启动时加载缓存", defaultValue = "false")
    public static final String NACOS_NAMING_LOAD_CACHE_AT_START = NACOS_PRE + "naming-load-cache-at-start";

    @Inject
    protected AppContext appContext;

    @Override
    public PropertiesProxy get() {
        return conf;
    }

    @Override
    public void init() throws Exception {
        NacosFactory.createNamingService(getNacosDiscoveryProperties());
    }

    public Properties getNacosDiscoveryProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, conf.get(NACOS_ADDR, ""));
        properties.put(NAMESPACE, conf.get(NACOS_NAMESPACE, ""));
        properties.put(UtilAndComs.NACOS_NAMING_LOG_NAME, conf.get(NACOS_LOG_FILENAME, ""));
        properties.put(UtilAndComs.NACOS_NAMING_LOG_LEVEL, conf.get(NACOS_LOG_LEVEL, ""));
        String endpoint = conf.get(NACOS_ENCODE_ENDPOINT, "");
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }
        properties.put(ACCESS_KEY, conf.get(NACOS_ACCESS_KEY, ""));
        properties.put(SECRET_KEY, conf.get(NACOS_SECRET_KEY, ""));
        properties.put(CLUSTER_NAME, conf.get(NACOS_CLUSTER_NAME, ""));
        properties.put(NAMING_LOAD_CACHE_AT_START, conf.get(NACOS_NAMING_LOAD_CACHE_AT_START, "false"));
        return properties;
    }
}
