package org.nutz.boot.starter.quartz;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
import org.nutz.json.Json;
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

    private int startupDelay = 0;

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
        startupDelay=conf.getInt("quartz.startupDelay",0);
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory(properties);
            scheduler = factory.getScheduler();
            scheduler.setJobFactory(new NutQuartzJobFactory(appContext.getIoc()));
            ((Ioc2) appContext.getIoc()).getIocContext().save("app", "scheduler", new ObjectProxy(scheduler));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return loader;
    }

    /**
     * 设置延迟加载秒数
     *
     * @param scheduler
     * @param startupDelay
     * @throws SchedulerException
     */
    protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
        if (startupDelay <= 0) {
            log.info("Starting Quartz Scheduler now");
            scheduler.start();
        } else {
            if (log.isInfoEnabled()) {
                log.info("Will start Quartz Scheduler [" + scheduler.getSchedulerName() +
                        "] in " + startupDelay + " seconds");
            }
            // Not using the Quartz startDelayed method since we explicitly want a daemon
            // thread here, not keeping the JVM alive in case of all other threads ending.
            Thread schedulerThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(startupDelay));
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        // simply proceed
                    }
                    if (log.isInfoEnabled()) {
                        log.info("Starting Quartz Scheduler now, after delay of " + startupDelay + " seconds");
                    }
                    try {
                        scheduler.start();
                    } catch (SchedulerException ex) {
                        throw new RuntimeException("Could not start Quartz Scheduler after delay", ex);
                    }
                }
            };
            schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    public void start() throws Exception {
        if (this.scheduler != null) {
            try {
                startScheduler(this.scheduler, this.startupDelay);
            } catch (SchedulerException ex) {
                throw new RuntimeException("Could not start Quartz Scheduler", ex);
            }
        }
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
