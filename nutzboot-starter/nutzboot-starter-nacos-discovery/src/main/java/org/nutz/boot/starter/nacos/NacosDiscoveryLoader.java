package org.nutz.boot.starter.nacos;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

import java.util.Map;
import java.util.Properties;

import org.nutz.boot.AppContext;
import org.nutz.boot.NbApp;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.tools.NbAppEventListener;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.hardware.NetworkItem;
import org.nutz.lang.hardware.Networks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/8
 */
@IocBean(create = "init")
public class NacosDiscoveryLoader implements ServerFace, NbAppEventListener {
    /**
     * 获取日志对象
     */
    private static final Log log = Logs.get();
    /**
     * Nacos配置项前缀
     */
    protected static final String NACOS_PRE = "nacos.discovery.";
    
    @PropDoc(value = "是否启用Nacos discovery", defaultValue = "true")
    public static final String NACOS_ENABLE = NACOS_PRE + "enable";
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

    @PropDoc(value = "Nacos 集群名称", defaultValue = "", need = true)
    public static final String NACOS_CLUSTER_NAME = NACOS_PRE + "cluster-name";

    @PropDoc(value = "Nacos 启动时加载缓存", defaultValue = "false")
    public static final String NACOS_NAMING_LOAD_CACHE_AT_START = NACOS_PRE + "naming-load-cache-at-start";

    @PropDoc(value = "Nacos 服务名", defaultValue = "")
    public static final String NACOS_NAMING_SERVISE_NAME = NACOS_PRE + "naming.service-name";

    @PropDoc(value = "Nacos 服务组", defaultValue = "")
    public static final String NACOS_NAMING_GROUP_NAME = NACOS_PRE + "naming.group-name";
    
    @PropDoc(value = "Nacos 服务名", defaultValue = "")
    public static final String NACOS_NAMING_CLUSTER_NAME = NACOS_PRE + "naming.cluster-name";

    @PropDoc(value = "Nacos 服务地址", defaultValue = "")
    public static final String NACOS_NAMING_IP = NACOS_PRE + "naming.ip";
    
    @PropDoc(value = "Nacos 服务端口", defaultValue = "")
    public static final String NACOS_NAMING_PORT = NACOS_PRE + "naming.port";

    @Inject
    protected AppContext appContext;
    
    @Inject
    protected PropertiesProxy conf;

    protected NamingService namingService;
    
    protected Properties properties = new Properties();
    

	String serviceName;
	String groupName;
	String clusterName;

    public void init() throws Exception {
    	if (conf.getBoolean(NACOS_ENABLE, true)) {
    		namingService = NacosFactory.createNamingService(getNacosDiscoveryProperties());
    		serviceName = conf.get(NACOS_NAMING_SERVISE_NAME, conf.get("nutz.application.name", conf.get("dubbo.application.name", "")));
    		groupName = conf.get(NACOS_NAMING_GROUP_NAME, "DEFAULT_GROUP");
    		clusterName = conf.get(NACOS_NAMING_CLUSTER_NAME, "public");
    		if (Strings.isBlank(serviceName)) {
    			log.info("require service name for nacos discovery!!! key=" + NACOS_NAMING_SERVISE_NAME);
    			throw new RuntimeException("require service name for nacos discovery! key=" + NACOS_NAMING_SERVISE_NAME);
    		}
    	}
    	else
    		log.info("Nacos discovery is disabled");
    }
    
    protected String ip;
    protected int port;
    
    public void afterAppStated(NbApp app) {
		if (namingService == null)
			return;
    	try {
			// 首先, 整体注册
			ip = conf.get(NACOS_NAMING_IP, Networks.ipv4());
			// 如果ip以*号结尾，则走前缀匹配逻辑
			if(ip.endsWith("*")) {
			    ip = getIpv4(ip.replace("*", ""));
            }
			port = conf.getInt(NACOS_NAMING_PORT, conf.getInt("server.port"));
			namingService.registerInstance(serviceName, groupName, ip, port, clusterName);
		} catch (NacosException e) {
			throw new RuntimeException(e);
		}
    };

    /**
     * 按前缀匹配获取本机ipv4地址
     * @param prefix
     * @return
     */
    private String getIpv4(String prefix) {
        Map<String, NetworkItem> items = Networks.networkItems();
        // 先遍历一次eth开头的
        for (int i = 0; i < 10; i++) {
            NetworkItem item = items.get("eth"+i);
            if (item != null) {
                String ip = item.getIpv4();
                if (Networks.ipOk(ip) && ip.startsWith(prefix))
                    return ip;
            }
        }
        for (NetworkItem item : items.values()) {
            String ip = item.getIpv4();
            if (Networks.ipOk(ip) && ip.startsWith(prefix))
                return ip;
        }
        return null;
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    	if (namingService != null) {
    		namingService.deregisterInstance(serviceName, ip, port, clusterName);
    	}
    }

    public Properties getNacosDiscoveryProperties() {
        properties.put(SERVER_ADDR, conf.get(NACOS_ADDR, "127.0.0.1:8848"));
        properties.put(NAMESPACE, conf.get(NACOS_NAMESPACE, "public"));
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
    
    @IocBean(name="nacosNamingService")
    public NamingService getNamingService() {
    	return namingService;
    }
}
