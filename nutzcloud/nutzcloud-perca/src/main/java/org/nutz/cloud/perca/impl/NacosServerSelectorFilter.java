package org.nutz.cloud.perca.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * 从nacos服务器获取目标服务的服务器列表,并转发
 *
 */
public class NacosServerSelectorFilter extends AbstractServerSelectorFilter implements EventListener {

	protected static final Log log = Logs.get();

	protected NamingService nacosNamingService;

	protected List<Instance> instances;

	@Override
	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		super.setPropertiesProxy(ioc, conf, prefix);
		if (Strings.isBlank(serviceName)) {
			throw new RuntimeException("nacos need service name!! prefix=" + prefix);
		}
		nacosNamingService = ioc.get(NamingService.class, "nacosNamingService");
		// 支持Group
		nacosNamingService.subscribe(serviceName, this);
		instances = nacosNamingService.selectInstances(serviceName, true);
		updateTargetServers(instances);
	}
	
	public void onEvent(Event event) {
		if (event instanceof NamingEvent) {
			instances = ((NamingEvent) event).getInstances();
			updateTargetServers(instances);
		}
	}

	protected void updateTargetServers(List<Instance> instances) {
		List<TargetServerInfo> infos = new ArrayList<TargetServerInfo>();
		for (Instance instance : instances) {
			TargetServerInfo info = new TargetServerInfo();
			info.host = instance.getIp();
			info.port = instance.getPort();
			infos.add(info);
		}
		super.targetServers = infos;
	}

	public String getType() {
		return "nacos";
	}

	@Override
	public void close() {
		if (nacosNamingService != null) {
			try {
				nacosNamingService.unsubscribe(serviceName, this);
			} catch (NacosException e) {
				log.warn("unsubscribe " + serviceName + "fail", e);
			}
		}
		super.close();
	}
}
