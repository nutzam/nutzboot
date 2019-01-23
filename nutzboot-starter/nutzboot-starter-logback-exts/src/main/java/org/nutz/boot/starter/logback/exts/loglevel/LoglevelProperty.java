package org.nutz.boot.starter.logback.exts.loglevel;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class LoglevelProperty {
    public final static String REDIS_KEY_PREFIX = "logback:loglevel:";
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 实例名称
     */
    private String name;
    /**
     * 进程ID
     */
    private String processId;
    /**
     * 心跳间隔
     */
    private int heartbeat;
    /**
     * 缓存时间
     */
    private int keepalive;
    /**
     * 日志等级
     */
    private String loglevel;

    private long uptime;

    private long vmFree;

    private long vmUse;

    private long vmTotal;

    private long vmMax;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public int getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(int keepalive) {
        this.keepalive = keepalive;
    }

    public String getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getVmFree() {
        return vmFree;
    }

    public void setVmFree(long vmFree) {
        this.vmFree = vmFree;
    }

    public long getVmUse() {
        return vmUse;
    }

    public void setVmUse(long vmUse) {
        this.vmUse = vmUse;
    }

    public long getVmTotal() {
        return vmTotal;
    }

    public void setVmTotal(long vmTotal) {
        this.vmTotal = vmTotal;
    }

    public long getVmMax() {
        return vmMax;
    }

    public void setVmMax(long vmMax) {
        this.vmMax = vmMax;
    }
}
