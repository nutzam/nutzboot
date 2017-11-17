package org.nutz.boot.starter.zbus;

import org.nutz.boot.AppContext;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.zbus.ZbusIocLoader;
import org.nutz.integration.zbus.rpc.ZbusClientBean;
import org.nutz.integration.zbus.rpc.ZbusServiceBootstrap;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ZbusStarter implements IocLoaderProvider, ServerFace {
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;
    
    public IocLoader getIocLoader() {
    	return new ZbusIocLoader();
    }

	public void start() throws Exception {
		if (conf.getBoolean("zbus.rpc.service.enable", false)) {
			if (!conf.has("zbus.rpc.service.packageNames")) {
				conf.put("zbus.rpc.service.packageNames", appContext.getMainClass().getPackage().getName() + ".service");
			}
			ioc.get(ZbusServiceBootstrap.class).start();
		}
		if (conf.getBoolean("zbus.rpc.client.enable", false)) {
			if (!conf.has("zbus.rpc.client.packageNames")) {
				conf.put("zbus.rpc.client.packageNames", appContext.getMainClass().getPackage().getName() + ".service");
			}
			ioc.get(ZbusClientBean.class);
		}
	}

	public void stop() throws Exception {
	}

	public boolean isRunning() {
		return false;
	}

	public boolean failsafe() {
		return false;
	}
    

}
