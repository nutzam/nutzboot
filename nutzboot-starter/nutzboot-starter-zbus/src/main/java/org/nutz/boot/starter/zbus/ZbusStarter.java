package org.nutz.boot.starter.zbus;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.zbus.rpc.ZbusClientBean;
import org.nutz.integration.zbus.rpc.ZbusServiceBootstrap;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ZbusStarter implements ServerFace {
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

	public void start() throws Exception {
		if (conf.getBoolean("zbus.rpc.service.enable", false)) {
			if (!conf.has("zbus.rpc.service.packageNames")) {
				conf.put("zbus.rpc.service.packageNames", appContext.getPackage() + ".service");
			}
			ioc.get(ZbusServiceBootstrap.class).start();
		}
		if (conf.getBoolean("zbus.rpc.client.enable", false)) {
			if (!conf.has("zbus.rpc.client.packageNames")) {
				conf.put("zbus.rpc.client.packageNames", appContext.getPackage() + ".service");
			}
			ioc.get(ZbusClientBean.class);
		}
		if (!conf.has("zbus.mq.packageNames"))
		    conf.put("zbus.mq.packageNames", appContext.getPackage());
	}

	public void stop() throws Exception {
	}

	public boolean isRunning() {
		return true;
	}

	public boolean failsafe() {
		return false;
	}
    

}
