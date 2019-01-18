package org.nutz.boot.starter.logback.exts.loglevel;

import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create = "init", depose = "depose")
public class LoglevelHeartbeatThread extends Thread {
    private static final Log log = Logs.get();
    private boolean keepRun = true;
    @Inject
    private LoglevelProperty loglevelProperty;
    @Inject
    private RedisService redisService;

    public LoglevelHeartbeatThread() throws Exception {
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (keepRun) {
                Lang.quiteSleep(loglevelProperty.getHeartbeat() * 1000);
                redisService.expire(LoglevelProperty.REDIS_KEY_PREFIX + "list:" + loglevelProperty.getName() + ":" + loglevelProperty.getProcessId(), loglevelProperty.getKeepalive());
            }
        } catch (Throwable e) {
            log.debug("something happen!!!", e);
        }
    }

    public void depose() throws Exception {
        this.keepRun = false;
    }

    public void init() throws Exception {
        this.keepRun = true;
    }
}
