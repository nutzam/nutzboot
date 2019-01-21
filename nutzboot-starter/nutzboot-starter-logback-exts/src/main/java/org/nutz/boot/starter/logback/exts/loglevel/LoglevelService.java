package org.nutz.boot.starter.logback.exts.loglevel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.slf4j.LoggerFactory;

@IocBean(create = "init")
public class LoglevelService implements PubSub {
    private static final Log log = Logs.get();
    @Inject
    protected LoglevelProperty loglevelProperty;
    @Inject
    protected RedisService redisService;
    @Inject
    protected PubSubService pubSubService;
    @Inject
    protected LoglevelHeartbeatThread loglevelHeartbeatThread;

    public void init() {
        pubSubService.reg(LoglevelProperty.REDIS_KEY_PREFIX + "pubsub", this);
        saveToRedis();
        doHeartbeat();
    }

    private void saveToRedis() {
        loglevelProperty.setLoglevel(getLevel());
        redisService.set(LoglevelProperty.REDIS_KEY_PREFIX + "list:" + loglevelProperty.getName() + ":" + loglevelProperty.getProcessId(), Json.toJson(loglevelProperty));
        redisService.expire(LoglevelProperty.REDIS_KEY_PREFIX + "list:" + loglevelProperty.getName() + ":" + loglevelProperty.getProcessId(), loglevelProperty.getKeepalive());
    }

    private void doHeartbeat() {
        loglevelHeartbeatThread.start();
    }

    public void changeLoglevel(LoglevelCommand loglevelCommand) {
        pubSubService.fire(LoglevelProperty.REDIS_KEY_PREFIX + "pubsub", Json.toJson(loglevelCommand, JsonFormat.compact()));
    }

    @Override
    public void onMessage(String channel, String message) {
        LoglevelCommand loglevelCommand = Json.fromJson(LoglevelCommand.class, message);
        //通过实例名称更改日志等级 或 通过进程ID更改日志等级
        if (("name".equals(loglevelCommand.getAction())
                && Strings.sNull(loglevelCommand.getName()).equals(loglevelProperty.getName())) ||
                ("processId".equals(loglevelCommand.getAction()) && Strings.sNull(loglevelCommand.getProcessId()).equals(loglevelProperty.getProcessId()))) {
            //更改之前
            System.out.println("logback loglevel change start.");
            testLevel();
            if (Strings.isNotBlank(loglevelCommand.getLevel())) {
                setLevel(loglevelCommand.getLevel());
                saveToRedis();
            }
            System.out.println("----------------------------");
            //更改之后
            testLevel();
            System.out.println("logback loglevel change end.");
        }
    }

    public boolean setLevel(String level) {
        boolean isSucceed = true;
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.getLogger("root").setLevel(Level.valueOf(level));
        } catch (Exception e) {
            e.printStackTrace();
            isSucceed = false;
        }
        return isSucceed;
    }

    public String getLevel() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            return loggerContext.getLogger("root").getLevel().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void testLevel() {
        log.info("info -- hello");
        log.warn("warn -- hello");
        log.error("error -- hello");
        log.debug("debug -- hello");
    }

}
