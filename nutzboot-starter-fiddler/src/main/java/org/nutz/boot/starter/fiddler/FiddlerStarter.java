package org.nutz.boot.starter.fiddler;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.fiddler.proxy.ProxyConfig;
import org.nutz.plugins.fiddler.proxy.ProxyType;
import org.nutz.plugins.fiddler.server.HttpProxyServer;

@IocBean
public class FiddlerStarter implements ServerFace {

	private final static Log log = Logs.get();

	public static final String PRE = "proxy.";

	@PropDoc(value = "服务器端口", defaultValue = "8080", type = "int")
	public static final String PROP_SRV_PORT = PRE + "srv_port";

	@PropDoc(value = "开关", defaultValue = "true", type = "boolean")
	public static final String PROP_ENABLE = PRE + "enable";

	@PropDoc(value = "代理方式", defaultValue = "http", type = "String")
	public static final String PROP_PROXY_TYPE_PORT = PRE + "proxy_type";

	@PropDoc(value = "开关", defaultValue = "false", type = "boolean")
	public static final String PROP_PROXY_ENABLE = PRE + "proxy.enable";

	@PropDoc(group = "代理地址", value = "ip", defaultValue = "127.0.0.1")
	public static final String PROP_PROXY_IP = PRE + "ip";
	@PropDoc(group = "代理端口", value = "port", defaultValue = "8118", type = "int")
	public static final String PROP_PROXY_PORT = PRE + "port";

	@Inject("refer:$ioc")
	protected Ioc ioc;

	@Inject
	protected PropertiesProxy conf;

	protected HttpProxyServer httpProxyServer;

	@IocBean(name = "httpProxyServer")
	public HttpProxyServer createHttpProxyServer() {
		HttpProxyServer httpProxyServer = new HttpProxyServer();
		if (conf.getBoolean(PROP_PROXY_ENABLE)) {
			switch (conf.get(PROP_PROXY_TYPE_PORT).trim().toUpperCase()) {
			case "HTTP": {
				httpProxyServer.proxyConfig(new ProxyConfig(ProxyType.HTTP, conf.get(PROP_PROXY_IP), conf.getInt(PROP_PROXY_PORT)));
				break;
			}
			case "SOCKS4": {
				httpProxyServer.proxyConfig(new ProxyConfig(ProxyType.SOCKS4, conf.get(PROP_PROXY_IP), conf.getInt(PROP_PROXY_PORT)));
				break;
			}
			case "SOCKS5": {
				httpProxyServer.proxyConfig(new ProxyConfig(ProxyType.SOCKS5, conf.get(PROP_PROXY_IP), conf.getInt(PROP_PROXY_PORT)));
				break;
			}
			default:
				break;
			}
		}
		return httpProxyServer;
	}

	public void start() throws Exception {
		if (conf.getBoolean(PROP_ENABLE, false)) {
			httpProxyServer = ioc.get(HttpProxyServer.class);
			new Thread(new Runnable() {

				@Override
				public void run() {
					httpProxyServer.start(conf.getInt(PROP_SRV_PORT));
					if (log.isInfoEnabled()) {
						log.infof("nutz-start-proxy start at port %s", conf.getInt(PROP_SRV_PORT));
					}
				}
			}).start();
		}
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public boolean isRunning() {
		return conf.getBoolean(PROP_ENABLE, false);
	}

	@Override
	public boolean failsafe() {
		return false;
	}
}
