package org.nutz.cloud.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.nutz.cloud.config.spi.ConfigureEventHandler;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.repo.Base64;

public class CloudConfig {

    protected static Properties selfConf;
    
    public static void init() {
        Properties selfConf = new Properties();
        InputStream ins = CloudConfig.class.getClassLoader().getResourceAsStream("META-INF/app.properties");
        if (ins != null) {
            try {
                selfConf.load(ins);
                selfConf.putIfAbsent("app.id", "SampleApp");
                selfConf.putIfAbsent("config.zone", "guest");
                selfConf.putIfAbsent("config.token", "123456");
                selfConf.putIfAbsent("config.label", "default");
                selfConf.putIfAbsent("config.type", "simple");
                selfConf.putIfAbsent("config.hosts", "nbconfig.nutz.cn");
            }
            catch (IOException e) {
                throw new RuntimeException("META-INF/app.properties can't read!", e);
            }
        }
        CloudConfig.selfConf = selfConf;
        
    }
    
    public static void fromRemote(PropertiesProxy conf, String fileName) {
        NutMap params = new NutMap();
        params.put("id", getSelfConfig("app.id", null));
        params.put("zone", getSelfConfig("app.zone", "guest"));
        params.put("token", getSelfConfig("config.token", "123456"));
        params.put("label", getSelfConfig("config.label", "default"));
        params.put("password", getSelfConfig("config.password", "ABC123456"));
        String hosts = getSelfConfig("config.hosts", null);
        String type = getSelfConfig("config.type", "simple");
        for (int i = 0; i < 5; i++) {
            for (String host : Strings.splitIgnoreBlank(hosts)) {
                try {
                    switch (type) {
                    // spring-cloud-config-server
                    case "spring":
                        bySpring(params, host);
                        return;
                    // 访问 eureka 获取服务器列表,然后按simple处理
                    case "eureku":
                        byEureka(params, host);
                        return;
                    // 简单模式, 也许就是nutzcloud-config-server的模块, 按路径取就行啦
                    case "simple":
                    default:
                        bySimple(conf, params, host, fileName);
                        return;
                    }
                }
                catch (Throwable e) {
                    Logs.get().infof("fail at type=%s host=%s", type, host);
                }
            }
        }
        throw new RuntimeException("can't fetch config from remote");
    }
    
    protected static void bySimple(PropertiesProxy conf, NutMap params, String host, String fileName) {
        String url = String.format("http://%s/%s/%s/%s/%s", host, params.get("zone"), params.get("id"), params.get("label"), fileName);
        try {
            Request req = Request.create(url, METHOD.GET);
            //long now = System.currentTimeMillis();
            //String once = R.UU32();
            //String sign = Lang.sha1(String.format("%s,%s,%s,%s,%s", params.get("zone"), params.get("id"), now, params.get("token"), once));
            //req.getHeader().set("X-Client-Once", once);
            //req.getHeader().set("X-Client-Time", now + "");
            //req.getHeader().set("X-Client-Sign", sign);
            req.getHeader().set("Authorization", Base64.encodeToString((params.get("zone") + ":" + params.getString("password")).getBytes(), false));
            Response resp = Sender.create(req).setTimeout(3000).send();
            if (resp.isOK()) {
                Logs.get().debug("load from " + url);
                conf.load(resp.getReader());
                return;
            }
            else {
                Logs.get().warnf("url=%s code=%s", url, resp.getStatus());
            }
        }
        catch (Exception e) {
            Logs.get().warn("url=" + url, e);
        }
        throw new RuntimeException("FAILED url=" + url);
    }
    
    protected static PropertiesProxy bySpring(NutMap params, String host) {
        throw Lang.noImplement();
    }
    
    protected static PropertiesProxy byEureka(NutMap params, String host) {
        throw Lang.noImplement();
    }

    public static void addListener(ConfigureEventHandler listener) {
        // TODO 完成事件监听
    }

    protected static String getSelfConfig(String key, String dftValue) {
        // 优先 -Dapp.id 然后是环境变量, 然后是内置的app.properties
        String value = System.getProperty(key, System.getenv(key));
        if (Strings.isBlank(value)) {
            value = selfConf.getProperty(key, dftValue);
        }
        return value;
    }
}
