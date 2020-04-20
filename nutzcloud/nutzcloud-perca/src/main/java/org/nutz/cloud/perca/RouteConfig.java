package org.nutz.cloud.perca;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.boot.AppContext;
import org.nutz.cloud.perca.impl.LoachRouteFilter;
import org.nutz.cloud.perca.impl.NacosRouteFilter;
import org.nutz.cloud.perca.impl.OldSimpleRouterFilter;
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
    
    @Inject
    protected AppContext appContext;
    
    protected List<RouteFilter> routes = new LinkedList<>();

    public Iterator<RouteFilter> getRouteFilters() {
        return routes.iterator();
    }
    
    public void init() throws Exception {
        for (String key : conf.getKeys()) {
            if (key.startsWith("gw.") && key.endsWith(".filters")) {
                String name = key.substring("gw.".length(), key.length() - ".filters".length());
                log.debug("add config for name=" + name);
                RouteFilter filter = null;
                String type = conf.get("gw."+name+".type", "simple");
                switch (type) {
				case "simple":
					filter = new SimpleRouteFilter();
					break;
				case "loach":
					filter = new LoachRouteFilter();
					break;
				case "nacos":
					filter = new NacosRouteFilter();
					break;
				default:
					// 可能是类名
					if (type.indexOf('.') > 0) 
						filter = (RouteFilter) appContext.getClassLoader().loadClass(type).newInstance();
					// 可能是老代码,虽然不太可能
					else if (conf.get("gw."+name+".type") == null) 
						filter = new OldSimpleRouterFilter();
					// 可能是ioc对象
					else if (type.startsWith("ioc:")) {
						filter = ioc.get(RouteFilter.class, type.substring(4));
					}
					// 啥都不是, 抛异常
					else
						throw new RuntimeException("bad gateway filter type");
					break;
				}
                filter.setPropertiesProxy(ioc, conf, key.substring(0, key.length() - ".filters".length()));
                routes.add(filter);
            }
        }
    }
}
