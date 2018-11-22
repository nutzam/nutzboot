package org.nutz.cloud.config;

import org.nutz.lang.Strings;

public class CloudConfigProperties {

    protected String app;
    protected String group;
    protected String key;
    protected String[] hosts;
    
    public String getApp() {
        return app;
    }
    public void setApp(String app) {
        this.app = app;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String[] getHosts() {
        return hosts;
    }
    public void setHosts(String[] hosts) {
        this.hosts = hosts;
    }
    
    public void check() {
        if (Strings.isBlank(app))
            throw new RuntimeException("miss app!!!");
        if (Strings.isBlank(group))
            throw new RuntimeException("miss group!!!");
        if (hosts == null || hosts.length == 0)
            throw new RuntimeException("miss hosts!!!");
    }
}
