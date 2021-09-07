package org.nutz.cloud.perca.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 固定服务器列表
 *
 */
public class SimpleRouteFilter extends AbstractServerSelectorFilter {

	protected static final Log log = Logs.get();

	protected String[] servers;

	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		super.setPropertiesProxy(ioc, conf, prefix);
		// 固定转发的服务器vip
		String str_servers = conf.get(prefix + ".servers");
		if (!Strings.isBlank(str_servers)) {
			this.servers = Strings.splitIgnoreBlank(str_servers, "(;|,)");
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
		}
		if (servers == null || servers.length == 0) {
			throw new RuntimeException("simple router need services list!! prefix=" + prefix);
		}
	}

	public String getType() {
		return "simple";
	}

}
