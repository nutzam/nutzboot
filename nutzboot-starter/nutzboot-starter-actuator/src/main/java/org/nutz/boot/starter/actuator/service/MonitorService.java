package org.nutz.boot.starter.actuator.service;

import java.util.List;

import org.nutz.boot.NbApp;
import org.nutz.boot.starter.MonitorObject;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

@IocBean
public class MonitorService {

    @Inject
    protected NbApp nbApp;
    
    protected List<MonitorObject> objs;

    public NutMap getMonitors() {
        if (objs == null)
            objs = nbApp.getAppContext().getBeans(MonitorObject.class);
        NutMap re = new NutMap();
        for (MonitorObject mon : objs) {
            if (!mon.isMonitorEnable())
                continue;
            re.put(mon.getMonitorName(), mon.getMonitors());
        }
        return re;
    }
}
