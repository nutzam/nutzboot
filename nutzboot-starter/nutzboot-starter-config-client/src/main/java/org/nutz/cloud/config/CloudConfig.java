package org.nutz.cloud.config;

import java.io.InputStream;

import org.nutz.cloud.config.spi.ConfigureEventHandler;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class CloudConfig {
    
    private static final Log log = Logs.get();

    public static CloudConfigProperties props;
    
    public static void init() {
        InputStream ins = CloudConfig.class.getClassLoader().getResourceAsStream("config-client.properties");
        if (ins != null) {
            log.info("Reading config-client.properties");
            PropertiesProxy pp = new PropertiesProxy(ins);
            props = pp.make(CloudConfigProperties.class, "config.client.");
        }
        else {
            log.info("Not found config-client.properties");
            props = new CloudConfigProperties();
        }
        if (!Strings.isBlank(System.getProperty("config.client.app")))
            props.app = System.getProperty("config.client.app");
        if (!Strings.isBlank(System.getProperty("config.client.group")))
            props.group = System.getProperty("config.client.group");
        if (!Strings.isBlank(System.getProperty("config.client.key")))
            props.key = System.getProperty("config.client.key");

        if (!Strings.isBlank(System.getProperty("config.client.hosts"))) {
            props.hosts = System.getProperty("config.client.hosts").split(",");
        }
        log.info("Loaded Cloud Config Properties :\r\n" + Json.toJson(props));
        props.check();
    }
    
    public static void fromRemote(PropertiesProxy conf, String fileName) {
        for (int i = 0; i < 5; i++) {
            for (String host : props.hosts) {
                boolean re = bySimple(conf, host, fileName);
                if (re)
                    return;
                Lang.quiteSleep(1000);
            }
        }
        throw new RuntimeException("can't fetch config from remote : " + fileName);
    }
    
    protected static boolean bySimple(PropertiesProxy conf, String host, String fileName) {
        // 首先, 获取版本号
        try {
            if (host.startsWith("http://") || host.startsWith("https://")) {
                // nop
            }
            else {
                host = "http://" + host;
            }
            if (!host.contains("/"))
                host += "/api/v1";
            String url = String.format("%s/%s/%s/version", host, props.group, props.app);
            Response resp = Http.get(url, 5000);
            if (resp.isOK()) {
                int version = Integer.parseInt(resp.getContent());
                // 版本号ok, 获取文件
                if (version > 0) {
                    url = String.format("http://%s/api/v1/%s/%s/%s/%s", host, props.group, props.app, version, fileName);
                    resp = Http.get(url, 5000);
                    if (resp.isOK()) {
                        conf.load(resp.getReader(), false);
                        return true;
                    }
                    else {
                        log.warn("config-server resp=" + resp.getStatus() + " when reqest " + url);
                    }
                }
                else {
                    log.warn("config-server version=" + version + " when reqest " + url);
                }
            }
            else {
                log.warn("config-server resp=" + resp.getStatus() + " when reqest " + url);
                return false;
            }
        }
        catch (Throwable e) {
            log.info("something happend", e);
        }
        return false;
    }

    public static void addListener(ConfigureEventHandler listener) {
        // TODO 完成事件监听
    }
}
