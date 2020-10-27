package org.nutz.cloud.perca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.boot.AppContext;
import org.nutz.cloud.perca.impl.*;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class RouterMaster implements Comparable<RouterMaster> {

	private static final Log log = Logs.get();

	protected String name;

	protected String prefix;

	protected String[] hostnames;

	protected String serviceName;

	protected String[] uriPrefixs;

	protected boolean removePrefix;

	protected Pattern uriPattern;

	protected int connectTimeOut, sendTimeOut, readTimeOut;

	protected boolean corsEnable;
	
	protected List<TargetServerInfo> targetServers;
	
	protected AppContext appContext;
	
	protected List<RouteFilter> filters;
	
	protected int priority;
	
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public boolean match(RouteContext ctx) {
		List<RouteFilter> filters = this.filters;
		for (RouteFilter filter : filters) {
			if (!filter.match(ctx))
				return false;
		}
		// 校验Host
		if (!checkHost(ctx))
			return false;
		// 校验uri前缀
		if (!checkUriPrefix(ctx))
			return false;
		// 校验uri正则表达式
		if (!checkUriPattern(ctx))
			return false;
		// 设置一些必要的超时设置
		ctx.connectTimeOut = connectTimeOut;
		ctx.sendTimeOut = sendTimeOut;
		ctx.readTimeOut = readTimeOut;
		// TODO 处理跨域
		return true;
	}
	
	public void preRoute(RouteContext ctx) throws IOException {
		ctx.rmaster = this;
		for (RouteFilter filter : filters) {
			filter.preRoute(ctx);
		}
	}
	
	public void postRoute(RouteContext ctx) throws IOException {
		ctx.rmaster = this;
		for (RouteFilter filter : filters) {
			filter.postRoute(ctx);
		}
	}

	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		this.name = prefix;
		// 需要匹配的域名
		String hostnames = conf.get(prefix + ".hostnames");
		if (!Strings.isBlank(hostnames)) {
			this.hostnames = Strings.splitIgnoreBlank(hostnames, "(;|,)");
		}
		
		// 服务名
		serviceName = conf.get(prefix + ".serviceName");

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
		priority = conf.getInt(prefix + ".priority", 0);
		
		// 处理拦截器
		List<RouteFilter> filters = new ArrayList<RouteFilter>();
        String types = conf.get(prefix+".filters", "simple");
        for (String type : Strings.splitIgnoreBlank(types)) {
    		RouteFilter filter = null;
            switch (type) {
    		case "simple":
    			filter = new SimpleRouteFilter();
    			break;
    		case "loach":
    			filter = new LoachServerSelectorFilter();
    			break;
    		case "nacos":
    			filter = new NacosServerSelectorFilter();
    			break;
    		case "nacos-prefix":
    			filter = new NacosServerPrefixSelectorFilter();
    			break;
    		case "sentinel":
    			filter = new SentinelFilter();
    			break;
    		case "hide-real-url":
				filter = new HideRealUrlFilter();
				break;
				case "rewrite-url":
					filter = new RewriteUrlFilter();
					break;
    		default:
    			// 可能是类名
    			if (type.indexOf('.') > 0) 
    				filter = (RouteFilter) appContext.getClassLoader().loadClass(type).newInstance();
    			// 可能是老代码,虽然不太可能
    			//else if (conf.get("gw."+name+".type") == null) 
    			//	filter = new OldSimpleRouterFilter();
    			// 可能是ioc对象
    			else if (type.startsWith("ioc:")) {
    				filter = ioc.get(RouteFilter.class, type.substring(4));
    			}
    			// 啥都不是, 抛异常
    			else
    				throw new RuntimeException("bad gateway filter type");
    			break;
    		}
            filter.setPropertiesProxy(ioc, conf, prefix);
            filters.add(filter);
		}
        this.filters = filters;
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
					ctx.matchedPrefix = prefix;
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

	@Override
	public int compareTo(RouterMaster o) {
		if (this.priority == o.priority) {
			return this.name.compareTo(o.name);
		}
		
		return Integer.compare(priority, o.priority);
	}
	
	public void depose() {
		for (RouteFilter filter : filters) {
			filter.close();
		}
	}

}
