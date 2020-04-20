package org.nutz.cloud.perca.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.cloud.perca.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * 从nacos服务器获取目标服务的服务器列表,并转发
 *
 */
public class NacosRouteFilter extends AbstractUrlRewriteRouterFilter {

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
		nacosNamingService.subscribe(serviceName, new EventListener() {

			public void onEvent(Event event) {
				if (event instanceof NamingEvent) {
					instances = ((NamingEvent) event).getInstances();
				}
			}
		});
		instances = nacosNamingService.selectInstances(serviceName, true);
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

	@Override
	protected boolean selectTargetServer(RouteContext ctx) {
		List<Instance> instances = this.instances; // 转为局部变量, 防范多线程下的竞争
		if (instances.isEmpty()) {
			return false;
		}
		Instance in = instances.get(R.random(0, instances.size())); // 支持自定义选择算法
		ctx.targetHost = in.toInetAddr();
		ctx.targetPort = in.getPort();
		return false;
	}

	public String getType() {
		return "nacos";
	}

}
