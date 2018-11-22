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
}
