package org.nutz.cloud.perca.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.cloud.perca.RouteContext;
import org.nutz.cloud.perca.RouteFilter;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class AbstractUrlRewriteRouterFilter implements RouteFilter {

	private static final Log log = Logs.get();

	protected String name;

	protected String prefix;

	protected String[] hostnames;

	protected String serviceName;

	protected String[] servers;

	protected String[] uriPrefixs;

	protected boolean removePrefix;

	protected Pattern uriPattern;

	protected int connectTimeOut, sendTimeOut, readTimeOut;

	protected boolean corsEnable;
	
	protected List<TargetServerInfo> targetServers;

	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		this.name = prefix;
		// 需要匹配的域名
		String hostnames = conf.get(prefix + ".hostnames");
		if (!Strings.isBlank(hostnames)) {
			this.hostnames = Strings.splitIgnoreBlank(hostnames, "(;|,)");
		}
		// 固定转发的服务器vip
		String servers = conf.get(prefix + ".servers");
		if (!Strings.isBlank(servers)) {
			this.servers = Strings.splitIgnoreBlank(servers, "(;|,)");
			List<TargetServerInfo> infos = new ArrayList<TargetServerInfo>();
			for (String server : this.servers) {
				String[] tmp = server.split(":");
				TargetServerInfo info = new TargetServerInfo();
				info.host = tmp[0];
				if (tmp.length > 1)
					info.port = Integer.parseInt(tmp[1]);
				infos.add(info);
			}
			this.targetServers = infos;
		} else {
			// 服务名
			serviceName = conf.get(prefix + ".serviceName");
		}

		String uriPrefixs = conf.get(prefix + ".uri.prefixs");
		if (!Strings.isBlank(uriPrefixs)) {
			this.uriPrefixs = Strings.splitIgnoreBlank(uriPrefixs, "(;|,)");
		}
		removePrefix = conf.getBoolean(prefix + ".uri.prefix.remove", false);
		String uripattern = conf.get(prefix + ".uri.match");
		if (!Strings.isBlank(uripattern)) {
			uriPattern = Pattern.compile(uripattern);
		}
		connectTimeOut = conf.getInt(prefix + ".time.connect", 2000);
		sendTimeOut = conf.getInt(prefix + ".time.send", 3000);
		readTimeOut = conf.getInt(prefix + ".time.read", 3000);
		corsEnable = conf.getBoolean(prefix + ".cors.enable");
	}

	@Override
	public boolean preRoute(RouteContext ctx) throws IOException {
		// 校验Host
		if (!checkHost(ctx))
			return true;
		// 校验uri前缀
		if (!checkUriPrefix(ctx))
			return true;
		// 校验uri正则表达式
		if (!checkUriPattern(ctx))
			return true;
		// 设置一些必要的超时设置
		ctx.connectTimeOut = connectTimeOut;
		ctx.sendTimeOut = sendTimeOut;
		ctx.readTimeOut = readTimeOut;
		// TODO 处理跨域

		if (!selectTargetServer(ctx)) {
			log.debugf("emtry server list for [%s]", serviceName);
			ctx.resp.sendError(500);
			return false; // 终止匹配
		}

		return RouteFilter.super.preRoute(ctx);
	}

	protected boolean selectTargetServer(RouteContext ctx) {
		List<TargetServerInfo> infos = this.targetServers;
		if (infos == null || infos.isEmpty())
			return false;
		int index = R.random(0, infos.size() - 1); // 支持各种算法
		TargetServerInfo info = infos.get(index);
		ctx.targetHost = info.host;
		if (info.port > 0)
			ctx.targetPort = info.port;
		return true;
	}

	public boolean checkHost(RouteContext ctx) {
		if (hostnames == null)
			return true;
		boolean pass = false;
		for (String hostname : hostnames) {
			if (hostname.equals(ctx.host)) {
				pass = true;
				break;
			}
		}
		return pass;
	}

	public boolean checkUriPrefix(RouteContext ctx) {
		if (uriPrefixs == null)
			return true;
		boolean pass = false;
		// 校验URL前缀
		if (uriPrefixs != null) {
			for (String prefix : uriPrefixs) {
				if (ctx.uri.startsWith(prefix)) {
					pass = true;
					if (removePrefix) {
						if (ctx.uri.length() == prefix.length()) {
							ctx.targetUri = "/";
						} else {
							ctx.targetUri = ctx.uri.substring(prefix.length());
						}
					}
				}
			}
		}
		return pass;
	}

	public boolean checkUriPattern(RouteContext ctx) {
		if (uriPattern == null)
			return true;
		return uriPattern.matcher(ctx.uri).find();
	}

	public String getName() {
		return name;
	}
}
