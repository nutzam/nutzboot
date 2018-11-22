package org.nutz.boot.starter.actuator.service;

import java.util.Collection;

import org.nutz.boot.starter.MonitorObject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

@IocBean(create="init")
public class JvmMonitorObject implements MonitorObject {
    
    protected NutMap re = new NutMap();

    public String getMonitorName() {
        return "jvm";
    }

    public Collection<String> getMonitorKeys() {
        return re.keySet();
    }

    public NutMap getMonitors() {
        updateMonitors();
        return re;
    }

    public void init() {
        re.put("pid", Lang.JdkTool.getProcessId("-1"));
        re.put("version", Lang.JdkTool.getMajorVersion());
        re.put("cores", Runtime.getRuntime().availableProcessors());
        updateMonitors();
    }
    
    public void updateMonitors() {
        Runtime runtime = Runtime.getRuntime();
        re.put("memory_free", runtime.freeMemory());
        re.put("memory_totol", runtime.totalMemory());
        re.put("memory_max", runtime.maxMemory());
    }
}
