package org.nutz.cloud.perca.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.boot.starter.loach.client.LoachClient;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 从Loach服务器获取目标服务的服务器列表,并转发
 *
 */
public class LoachServerSelectorFilter extends AbstractServerSelectorFilter implements LoachClient.UpdateListener {

    protected static final Log log = Logs.get();
    
    protected LoachClient loachClient;

    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        super.setPropertiesProxy(ioc, conf, prefix);
        if (Strings.isBlank(serviceName)) {
        	throw new RuntimeException("loach need service name!! prefix=" + prefix);
        }
        if (loachClient != null) {
            loachClient = ioc.get(LoachClient.class);
            loachClient.addListener(this);
        }
        updateTargetServers(loachClient.getService(serviceName));
    }
    
    public void onUpdate(Map<String, List<NutMap>> services) {
		updateTargetServers(services.get(serviceName));
	}

    protected void updateTargetServers(List<NutMap> list) {
    	if (list == null || list.isEmpty()) {
    		//this.targetServers = new ArrayList<TargetServerInfo>();
    		return;
    	}
    	List<TargetServerInfo> infos = new ArrayList<TargetServerInfo>();
    	for (NutMap service : list) {
			TargetServerInfo info = new TargetServerInfo();
			info.host = service.getString("vip");
	        info.port = service.getInt("port");
	        infos.add(info);
		}
    	this.targetServers = infos;
    }

    public String getType() {
        return "loach";
    }
}
