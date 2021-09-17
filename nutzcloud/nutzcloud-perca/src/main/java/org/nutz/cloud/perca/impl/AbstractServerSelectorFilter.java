package org.nutz.cloud.perca.impl;

import java.io.IOException;
import java.util.List;

import org.nutz.cloud.perca.RouteContext;
import org.nutz.cloud.perca.RouteFilter;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class AbstractServerSelectorFilter implements RouteFilter, AutoCloseable {

	private static final Log log = Logs.get();

	protected String name;

	protected String serviceName;

	protected List<TargetServerInfo> targetServers;

	private static final String GRAYSCALE_SWITCH = "grayscale.switch";
	private static final String GRAYSCALE_HOST = "grayscale.host";
	private static final String GRAYSCALE_PORT = "grayscale.port";
	private static final String GRAYSCALE_KEYS = "grayscale.keys";
	private int grayScale;
	private String grayScaleHost;
	private int grayScalePort;
	private String[] grayScaleKeys;

	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		this.name = prefix;
		serviceName = conf.get(prefix + ".serviceName");

		this.grayScale = conf.getInt(GRAYSCALE_SWITCH, 0);
		if (this.grayScale == 1) {
			this.grayScaleHost = conf.get(GRAYSCALE_HOST, "");
			this.grayScalePort = conf.getInt(GRAYSCALE_PORT, 0);
			this.grayScaleKeys = Strings.splitIgnoreBlank(conf.get(GRAYSCALE_KEYS, ""));
			if (Strings.isBlank(this.grayScaleHost)) {
				throwNotConfigException(GRAYSCALE_HOST);
			}
			if (grayScalePort == 0) {
				throwNotConfigException(GRAYSCALE_PORT);
			}
			if(this.grayScaleKeys.length == 0) {
				throwNotConfigException(GRAYSCALE_KEYS);
			}
		}
	}

	private void throwNotConfigException(String configName) {
		throw new RuntimeException(configName + " is not config...");
	}

	@Override
	public boolean preRoute(RouteContext ctx) throws IOException {
		if (!selectTargetServer(ctx, this.targetServers)) {
			log.debugf("emtry server list for [%s]", serviceName);
			ctx.resp.sendError(500);
			return false; // 终止匹配
		}

		return true;
	}

	protected boolean selectTargetServer(RouteContext ctx, List<TargetServerInfo> infos) {
		if (infos == null || infos.isEmpty())
			return false;
		int index = R.random(0, infos.size() - 1); // 支持各种算法
		if (index == infos.size()) {
			index = infos.size() - 1;
		}
		if(this.grayScale == 1 && "GET".equals(ctx.method.toUpperCase()) && checkKeys(ctx.queryString, this.grayScaleKeys)) {
			ctx.targetHost = this.grayScaleHost;
			ctx.targetPort= this.grayScalePort;
			if(log.isDebugEnabled()) {
				log.debugf("grayscale forward reqest %s to %s:%s", ctx.uri, ctx.targetHost, ctx.targetPort);
			}
		} else {
			TargetServerInfo info = infos.get(index);
			ctx.targetHost = info.host;
			if (info.port > 0)
				ctx.targetPort = info.port;
			if (log.isDebugEnabled())
				log.debugf("forward reqest %s to %s:%s", ctx.uri, ctx.targetHost, ctx.targetPort);
		}
		return true;
	}

	private boolean checkKeys(String data, String[] keys) {
		for (String key : keys) {
			if(Strings.isNotBlank(data) && data.contains(key)) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}
	
	public void close() {
		
	}
}
