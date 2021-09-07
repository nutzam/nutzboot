package org.nutz.boot.starter.quartz;

import java.util.concurrent.TimeUnit;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.quartz.QuartzManager;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

@IocBean
public class QuartzStarter implements ServerFace {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @Inject
    protected Scheduler scheduler;

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
        int startupDelay = conf.getInt("quartz.startupDelay",0);
        try {
            startScheduler(this.scheduler, startupDelay);
        } catch (SchedulerException ex) {
            throw new RuntimeException("Could not start Quartz Scheduler", ex);
        }
        appContext.getIoc().get(QuartzManager.class);
        appContext.getIoc().get(NutQuartzCronJobFactory.class);
    }

    public void stop() throws Exception {
        if (scheduler != null)
            scheduler.shutdown(true);
    }

}
