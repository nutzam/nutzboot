package org.nutz.boot.starter.nacos;

import java.io.IOException;
import java.util.Properties;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.config.impl.AbstractConfigureLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;

/**
 * @author wentao
 * @author wendal
 * @email wentao0291@gmail.com
 * @email wendal1985@gmail.com
 * @date 2019-03-06 21:45
 */
@IocBean
public class NacosConfigureLoader extends AbstractConfigureLoader {

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
    @PropDoc(value = "Nacos远程地址", defaultValue="127.0.0.1:8848")
    public static final String NACOS_ADDR = NACOS_PRE + "addr";
    /**
     * Nacos Data ID 配置项
     */
    @PropDoc(value = "Nacos Data ID", defaultValue="nutzboot")
    public static final String NACOS_DATA_ID = NACOS_PRE + "data_id";
    /**
     * Nacos分组配置项
     */
    @PropDoc(value = "Nacos分组", defaultValue="DEFAULT_GROUP")
    public static final String NACOS_GROUP = NACOS_PRE + "group";
    /**
     * Nacos数据类型配置项（用于识别使用哪种方式解析配置项） 支持配置： json, properties, xml
     */
    @PropDoc(value = "Nacos数据类型", defaultValue="properties")
    public static final String NACOS_DATA_TYPE = NACOS_PRE + "data_type";

    @Inject
    protected AppContext appContext;

    @Override
    public PropertiesProxy get() {
        return conf;
    }

    private void setConfig(String content, String contentType, PropertiesProxy conf) {
        if ("json".equals(contentType)) {
            NutMap configMap = new NutMap(content);
            conf.putAll(configMap);
        } else if ("xml".equals(contentType)) {
            Properties properties = new Properties();
            try {
                properties.loadFromXML(new StringInputStream(content));
                for (Object key : properties.keySet()) {
                    conf.put(key.toString(), properties.get(key).toString());
                }
            }
            catch (IOException e) {
                throw Lang.makeThrow("nacos config xml parse error!");
            }
        } else if ("properties".equals(contentType) || "txt".equals(contentType)) {
            PropertiesProxy propertiesProxy = new PropertiesProxy(new StringInputStream(content));
            conf.putAll(propertiesProxy);
        } else {
            throw Lang.makeThrow("nacos.config.data_type is not found or not recognize，only json,xml and properties are support!");
        }
    }

    @Override
    public void init() throws Exception {
        conf.setPaths("application.properties");
        String serverAddr = conf.get(NACOS_ADDR, "127.0.0.1:8848");
        String dataId = conf.get(NACOS_DATA_ID, "nutzboot");
        String group = conf.get(NACOS_GROUP, "DEFAULT_GROUP");
        String dataType = conf.get(NACOS_DATA_TYPE, "properties");

        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String configInfo = configService.getConfig(dataId, group, 5000);
        log.debugf("get nacos config：%s", configInfo);
        if (Strings.isNotBlank(configInfo)) {
            setConfig(configInfo, dataType, conf);
        }
    }
}
