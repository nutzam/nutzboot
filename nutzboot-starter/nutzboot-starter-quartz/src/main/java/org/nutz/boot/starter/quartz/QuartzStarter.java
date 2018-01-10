package org.nutz.boot.starter.quartz;

import org.nutz.boot.AppContext;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.quartz.QuartzIocLoader;
import org.nutz.integration.quartz.QuartzManager;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class QuartzStarter implements IocLoaderProvider, ServerFace {
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

	public IocLoader getIocLoader() {
		if (!conf.has("cron.pkgs")) {
			conf.put("cron.pkgs", appContext.getPackage());
		}
		return new QuartzIocLoader();
	}

	public void start() throws Exception {

		appContext.getIoc().get(QuartzManager.class);
		appContext.getIoc().get(NutQuartzCronJobFactory.class);
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
