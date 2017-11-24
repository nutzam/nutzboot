package org.nutz.boot.starter.ngrok;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.ngrok.client.NgrokClient;

@IocBean
public class NgrokClientStarter implements ServerFace {
	
	public static final String PRE = "ngrok.client.";

	@PropDoc(value="服务器域名", defaultValue="wendal.cn")
	public static final String PROP_SRV_HOST = PRE + "srv_host";
	
	@PropDoc(value="服务器端口", defaultValue="4443", type="int")
	public static final String PROP_SRV_PORT = PRE + "srv_port";
	
	@PropDoc(value="目标端口", defaultValue="8080", type="int")
	public static final String PROP_TO_PORT = PRE + "to_port";
	
	@PropDoc(value="秘钥", need=true)
	public static final String PROP_AUTH_TOKEN = PRE + "auth_token";
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	protected NgrokClient ngrokClient;
	
	@IocBean(name="ngrokClient")
	public NgrokClient createNgrokClient() {
		NgrokClient ngrokClient = new NgrokClient();
		ngrokClient.auth_token = conf.check(PROP_AUTH_TOKEN);
		if (conf.has(PROP_SRV_HOST)) {
			ngrokClient.hostname = conf.get(PROP_SRV_HOST);
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
		ngrokClient = ioc.get(NgrokClient.class);
		ngrokClient.start();
	}

	@Override
	public void stop() throws Exception {
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
