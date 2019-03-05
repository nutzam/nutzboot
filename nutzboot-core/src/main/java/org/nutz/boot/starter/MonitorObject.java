package org.nutz.boot.starter;

import java.util.Arrays;
import java.util.Collection;

import org.nutz.lang.util.NutMap;

public interface MonitorObject {
    
    String getMonitorName();
    
    default boolean isMonitorEnable() {
        return true;
    }

    default Collection<String> getMonitorKeys() {
        return Arrays.asList();
    }
    
    default Object getMonitorValue(String key) {
        return null;
    }
    
    default NutMap getMonitors() {
        NutMap re = new NutMap();
        for (String key : getMonitorKeys()) {
            re.put(key, getMonitorValue(key));
        }
        return re;
    }
    
    default StringBuilder getMonitorForPrint() {
        String LR = "\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------").append(LR);
        for (String key : getMonitorKeys()) {
            sb.append(String.format("%-40s : %s", key, getMonitorValue(key))).append(LR);
        }
        sb.append("----------------------------------------------");
        return sb;
    }
}
