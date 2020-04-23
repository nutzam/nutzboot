package org.nutz.cloud.perca.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.nutz.cloud.perca.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;

/**
 * 从nacos服务器获取目标服务的服务器列表,并转发
 *
 */
public class NacosServerPrefixSelectorFilter extends AbstractServerSelectorFilter {

	protected static final Log log = Logs.get();

	protected NamingService nacosNamingService;

	protected String serviceNamePrefix;

	protected Map<String, TService> serviceMap;

	protected ExecutorService es;

	protected boolean running;
	
	protected String group;

	public class TService {
		String name;
		String prefix;
		List<Instance> instances;
		EventListener listener;
		List<TargetServerInfo> targetServers;
	}

	public NacosServerPrefixSelectorFilter() {
		es = Executors.newFixedThreadPool(1);
		running = true;
		serviceMap = new HashMap<String, NacosServerPrefixSelectorFilter.TService>();
	}

	@Override
	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		super.setPropertiesProxy(ioc, conf, prefix);
		serviceNamePrefix = conf.check(prefix + ".serviceNamePrefix");
		nacosNamingService = ioc.get(NamingService.class, "nacosNamingService");
		group = conf.get(prefix + ".nacos_prefix.group", Constants.DEFAULT_GROUP);
		int loopTime = conf.getInt(prefix + ".loopTime", 3000);
		es.submit(new Runnable() {
			public void run() {
				OUT: while (running) {
					Lang.quiteSleep(loopTime);
					if (!running) {
						break;
					}
					try {
						int pageNumber = 1;
						int pageSize = 100;
						Set<String> serviceNames = new HashSet<>();
						while (true) {
							ListView<String> names = nacosNamingService.getServicesOfServer(pageNumber, pageSize, group);
							for (String name : names.getData()) {
								if (name.startsWith(serviceNamePrefix)) {
									serviceNames.add(name);
								}
							}
							if (names.getCount() != pageSize) {
								break;
							}
						}
						Set<String> lastNames = new HashSet<String>(serviceMap.keySet());
						log.debug("找到的服务名列表为: " + Json.toJson(serviceNames));
						boolean hasDiff = false;
						for (String name : serviceNames) {
							if (!lastNames.remove(name)) {
								hasDiff = true;
								break;
							}
						}
						if (!hasDiff && lastNames.isEmpty()) {
							continue OUT; // 没有变更. 可以睡了
						}
						// 有变更, 需要构建新的map了
						Map<String, TService> newServiceMap = new HashMap<>();
						Map<String, TService> oldServiceMap = serviceMap;
						try {
							for (String name : serviceNames) {
								newServiceMap.put(name, setupTService(name));
							}
						} catch (Throwable e) {
							log.warn("配置新的服务名列表时报错了", e);
							// 清理已注册的监听器
							for (TService ts : newServiceMap.values()) {
								nacosNamingService.unsubscribe(ts.name, ts.listener);
							}
							// 跳出循环
							continue OUT;
						}
						// 替换服务映射表
						serviceMap = newServiceMap;
						// 反注册老的服务映射表
						for (TService ts : oldServiceMap.values()) {
							if (ts.listener != null) {
								nacosNamingService.unsubscribe(ts.name, ts.listener);
							}
						}
					} catch (Throwable e) {
						log.warn("轮询服务器列表失败", e);
					}
				}
			}
		});
	}

	// TODO 支持Group
	public TService setupTService(String name) throws NacosException {
		TService ts = new TService();
		ts.name = name;
		ts.instances = nacosNamingService.getAllInstances(name, group);
		ts.prefix = "/" + ts.name.substring(serviceNamePrefix.length());
		ts.listener = new EventListener() {
			public void onEvent(Event event) {
				NacosServerPrefixSelectorFilter.this.onEvent(event, ts);
			}
		};
		nacosNamingService.subscribe(name, group, ts.listener);
		updateTargetServers(ts.instances, ts);
		return ts;
	}

	public void onEvent(Event event, TService ts) {
		if (event instanceof NamingEvent) {
			updateTargetServers(((NamingEvent) event).getInstances(), ts);
		}
	}

	protected void updateTargetServers(List<Instance> instances, TService ts) {
		List<TargetServerInfo> infos = new ArrayList<TargetServerInfo>();
		for (Instance instance : instances) {
			TargetServerInfo info = new TargetServerInfo();
			info.host = instance.getIp();
			info.port = instance.getPort();
			infos.add(info);
		}
		ts.targetServers = infos;
	}

	public String getType() {
		return "nacos";
	}

	@Override
	public void close() {
		running = false;
		es.shutdown();
		try {
			es.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("等待线程池退出,但超时了", e);
		}
		for (TService ts : serviceMap.values()) {
			if (ts.listener != null) {
				try {
					nacosNamingService.unsubscribe(ts.name, ts.listener);
				} catch (NacosException e) {
					log.info("反注册事件监听器报错,可能会导致内存泄漏", e);
				}
			}
		}
	}

	public boolean match(RouteContext ctx) {
		Map<String, TService> serviceMap = this.serviceMap;
		for (TService ts : serviceMap.values()) {
			if (ctx.uri.startsWith(ts.prefix)) {
				ctx.matchedPrefix = ts.prefix;
				ctx.obj = ts;
				return true;
			}
		}
		return false;
	}

	public boolean preRoute(RouteContext ctx) throws IOException {
		TService ts = (TService) ctx.obj;
		if (!selectTargetServer(ctx, ts.targetServers)) {
			log.debugf("emtry server list for [%s]", serviceName);
			ctx.resp.sendError(500);
			return false; // 终止匹配
		}
		return true;
	}
}
