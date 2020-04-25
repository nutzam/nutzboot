package org.nutz.cloud.perca;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;

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
    
    public void reload() throws Exception {
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
        List<RouterMaster> oldMasters = this.masters;
        this.masters = masters;
        if (oldMasters != null && oldMasters.size() > 0) {
        	for (RouterMaster routerMaster : oldMasters) {
				routerMaster.depose();
			}
        }
    }
    
    public void init() throws Exception {
    	reload();
    	if (ioc.has("nacosConfigService")) {
    		ConfigService cs = ioc.get(ConfigService.class, "nacosConfigService");
    		cs.addListener(conf.check("nacos.config.data-id"), conf.get("nacos.config.group", Constants.DEFAULT_GROUP), new Listener() {
				
				@Override
				public void receiveConfigInfo(String configInfo) {
					try {
						reload();
					} catch (Exception e) {
						log.error("fail to reload config!!!", e);
					}
				}
				
				@Override
				public Executor getExecutor() {
					return null;
				}
			});
    	}
    }
    
    
}
