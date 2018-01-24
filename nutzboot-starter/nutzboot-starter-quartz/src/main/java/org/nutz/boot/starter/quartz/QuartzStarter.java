package org.nutz.boot.starter.quartz;

import java.util.Properties;

import org.nutz.boot.AppContext;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.quartz.NutQuartzJobFactory;
import org.nutz.integration.quartz.QuartzIocLoader;
import org.nutz.integration.quartz.QuartzManager;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@IocBean
public class QuartzStarter implements IocLoaderProvider, ServerFace {
    
    private static final Log log = Logs.get();
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;
	
	protected Scheduler scheduler;

	public IocLoader getIocLoader() {
		if (!conf.has("cron.pkgs")) {
			conf.put("cron.pkgs", appContext.getPackage());
		}
		QuartzIocLoader loader = new QuartzIocLoader();
		if (appContext.getClassLoader().getResource("quartz.properties") != null) {
		    log.debug("found quartz.properties, use it");
		    return loader;
		}
		// 通过nutzboot的配置信息来初始化
		Properties properties = new Properties();
		for (String key : conf.keySet()) {
            if (key.startsWith("quartz.")) {
                properties.put("org." + key, conf.get(key));
            }
        }
		// 设置一下默认值
		/*
org.quartz.scheduler.instanceName = NutzbookScheduler 
org.quartz.threadPool.threadCount = 3 
org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
org.quartz.scheduler.skipUpdateCheck=true
		 */
        properties.putIfAbsent("org.quartz.scheduler.instanceName", "NutzbootScheduler");
        properties.putIfAbsent("org.quartz.threadPool.threadCount", "8");
        properties.putIfAbsent("org.quartz.scheduler.skipUpdateCheck", "true");
		try {
            StdSchedulerFactory factory = new StdSchedulerFactory(properties);
            scheduler = factory.getScheduler();
            scheduler.setJobFactory(new NutQuartzJobFactory(appContext.getIoc()));
            ((Ioc2)appContext.getIoc()).getIocContext().save("app", "scheduler", new ObjectProxy(scheduler));
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
		return loader;
	}

	public void start() throws Exception {
	    if (scheduler != null)
	        scheduler.start();
		appContext.getIoc().get(QuartzManager.class);
		appContext.getIoc().get(NutQuartzCronJobFactory.class);
	}

	public void stop() throws Exception {
	    if (scheduler != null)
	        scheduler.shutdown(true);
	}

	public boolean isRunning() {
		return true;
	}

	public boolean failsafe() {
		return false;
	}

    
}
