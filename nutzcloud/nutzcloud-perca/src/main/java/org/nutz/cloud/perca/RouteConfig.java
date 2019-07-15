package org.nutz.cloud.perca;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.cloud.perca.impl.SimpleRouteFilter;
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
    
    protected List<RouteFilter> routes = new LinkedList<>();

    public Iterator<RouteFilter> getRouteFilters() {
        return routes.iterator();
    }
    
    public void init() {
        for (String key : conf.getKeys()) {
            if (key.startsWith("gw.") && key.endsWith(".filters")) {
                String name = key.substring("gw.".length(), key.length() - ".filters".length());
                log.debug("add config for name=" + name);
                // 当前仅支持simple,直接new就行了
                SimpleRouteFilter simple = new SimpleRouteFilter();
                simple.setPropertiesProxy(ioc, conf, key.substring(0, key.length() - ".filters".length()));
                routes.add(simple);
            }
        }
    }
}
