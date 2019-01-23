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
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.slf4j.LoggerFactory;

import java.util.Set;

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

    /**
     * 初始化数据到redis并订阅主题
     */
    public void init() {
        pubSubService.reg(LoglevelProperty.REDIS_KEY_PREFIX + "pubsub", this);
        saveToRedis();
        doHeartbeat();
    }

    public void saveToRedis() {
        long vmFree = 0;
        long vmUse = 0;
        long vmTotal = 0;
        long vmMax = 0;
        int byteToMb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        vmTotal = rt.totalMemory() / byteToMb;
        vmFree = rt.freeMemory() / byteToMb;
        vmMax = rt.maxMemory() / byteToMb;
        vmUse = vmTotal - vmFree;
        loglevelProperty.setVmTotal(vmTotal);
        loglevelProperty.setVmFree(vmFree);
        loglevelProperty.setVmMax(vmMax);
        loglevelProperty.setVmUse(vmUse);
        loglevelProperty.setLoglevel(getLevel());
        redisService.set(LoglevelProperty.REDIS_KEY_PREFIX + "list:" + loglevelProperty.getName() + ":" + loglevelProperty.getProcessId(), Json.toJson(loglevelProperty, JsonFormat.compact()));
        redisService.expire(LoglevelProperty.REDIS_KEY_PREFIX + "list:" + loglevelProperty.getName() + ":" + loglevelProperty.getProcessId(), loglevelProperty.getKeepalive());
    }

    /**
     * 启动心跳线程
     */
    private void doHeartbeat() {
        loglevelHeartbeatThread.start();
    }

    /**
     * 发送消息
     *
     * @param loglevelCommand
     */
    public void changeLoglevel(LoglevelCommand loglevelCommand) {
        pubSubService.fire(LoglevelProperty.REDIS_KEY_PREFIX + "pubsub", Json.toJson(loglevelCommand, JsonFormat.compact()));
    }

    /**
     * 消息处理
     *
     * @param channel
     * @param message
     */
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

    /**
     * 设置当前进程日志等级
     *
     * @param level
     * @return
     */
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

    /**
     * 获取当前进程日志等级
     *
     * @return
     */
    public String getLevel() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            return loggerContext.getLogger("root").getLevel().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 测试日志等级
     */
    private void testLevel() {
        log.info("info -- hello");
        log.warn("warn -- hello");
        log.error("error -- hello");
        log.debug("debug -- hello");
    }

    /**
     * 获取客户端列表
     *
     * @return
     */
    public NutMap getData() {
        Set<String> set = redisService.keys("logback:loglevel:list:*");
        NutMap map = NutMap.NEW();
        for (String key : set) {
            String[] keys = key.split(":");
            String name = keys[3];
            LoglevelProperty loglevelProperty = Json.fromJson(LoglevelProperty.class, redisService.get(key));
            map.addv2(name, loglevelProperty);
        }
        return map;
    }

}
