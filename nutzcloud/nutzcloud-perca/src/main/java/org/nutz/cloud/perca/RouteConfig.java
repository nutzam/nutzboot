package org.nutz.cloud.perca;

import java.util.LinkedList;
import java.util.List;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init")
public class RouteConfig {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected AppContext appContext;
    
    protected List<RouterMaster> masters = new LinkedList<>();

    public List<RouterMaster> getRouteMasters() {
        return masters;
    }
    
    public void init() throws Exception {
    	List<RouterMaster> masters = new LinkedList<>();
        for (String key : conf.getKeys()) {
            if (key.startsWith("gw.") && key.endsWith(".filters")) {
                String name = key.substring("gw.".length(), key.length() - ".filters".length());
                RouterMaster master = new RouterMaster();
                master.setAppContext(appContext);
                master.setPropertiesProxy(ioc, conf, "gw." + name);
                masters.add(master);
            }
        }
        log.debugf("master count=%d", masters.size());
        this.masters = masters;
    }
}
