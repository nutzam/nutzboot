package org.nutz.boot.starter.ngrok;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.client.NgrokClient;

@IocBean
public class NgrokClientStarter implements ServerFace {
    
    private static final Log log = Logs.get();
	
	public static final String PRE = "ngrok.client.";

	@PropDoc(value="服务器域名", defaultValue="wendal.cn")
	public static final String PROP_SRV_HOST = PRE + "srv_host";
	
	@PropDoc(value="服务器端口", defaultValue="4443", type="int")
	public static final String PROP_SRV_PORT = PRE + "srv_port";

    @PropDoc(value="期望的域名")
    public static final String PROP_HOSTNAME = PRE + "hostname";
	
	@PropDoc(value="目标端口", defaultValue="8080", type="int")
	public static final String PROP_TO_PORT = PRE + "to_port";
	
	@PropDoc(value="秘钥", defaultValue="4kg9lckq5og4ip02j736e3i7ku")
	public static final String PROP_AUTH_TOKEN = PRE + "auth_token";
	
	@PropDoc(value="开关", defaultValue="true", type="boolean")
	public static final String PROP_ENABLE = PRE + "enable";
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	protected NgrokClient ngrokClient;
	
	@IocBean(name="ngrokClient")
	public NgrokClient createNgrokClient() {
		NgrokClient ngrokClient = new NgrokClient();
		ngrokClient.auth_token = conf.get(PROP_AUTH_TOKEN, "4kg9lckq5og4ip02j736e3i7ku");
		if ("4kg9lckq5og4ip02j736e3i7ku".equals(ngrokClient.auth_token)) {
            log.info("using default ngrok auth_token, hostname will be change every startup.");
            log.info("if you want a fixed hostname, pls login in https://nutz.cn and found your ngrok token in user homepage");
		}
        if (conf.has(PROP_HOSTNAME)) {
            ngrokClient.hostname = conf.get(PROP_HOSTNAME);
        }
        if (conf.has(PROP_SRV_HOST)) {
            ngrokClient.srv_host = conf.get(PROP_SRV_HOST);
        }
		if (conf.has(PROP_SRV_PORT)) {
			ngrokClient.srv_port = conf.getInt(PROP_SRV_PORT);
		}
		if (conf.has(PROP_TO_PORT)) {
			ngrokClient.to_port = conf.getInt(PROP_TO_PORT);
		}
		return ngrokClient;
	}

	public void start() throws Exception {
		if(conf.getBoolean(PROP_ENABLE, true)) {
			ngrokClient = ioc.get(NgrokClient.class);
			ngrokClient.start();
		}
	}

	@Override
	public void stop() throws Exception {
		if (ngrokClient != null)
			ngrokClient.stop();
	}

	@Override
	public boolean isRunning() {
		return ngrokClient != null && ngrokClient.status == 1;
	}

	@Override
	public boolean failsafe() {
		return false;
	}
}
