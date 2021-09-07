package org.nutz.cloud.perca.impl;

import java.io.IOException;

import org.eclipse.jetty.client.HttpResponseException;
import org.nutz.cloud.perca.RouteContext;
import org.nutz.cloud.perca.RouteFilter;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

import com.alibaba.csp.sentinel.AsyncEntry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class SentinelFilter implements RouteFilter {
	
	protected String name;

	public String getName() {
		return name;
	}

	public String getType() {
		return "sentinel";
	}

	public void close() {
	}
	
	@Override
	public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
		this.name = prefix;
	}

	@Override
	public boolean preRoute(RouteContext ctx) throws IOException {
		String apiName = Strings.sBlank(ctx.serviceName, ctx.matchedPrefix);
		if (Strings.isBlank(apiName))
			apiName = "default";
		ApiDefinition api = GatewayApiDefinitionManager.getApiDefinition(apiName);
		if (api != null) {
			 try {
				AsyncEntry entry = SphU.asyncEntry(apiName, ResourceTypeConstants.COMMON_API_GATEWAY, EntryType.IN, new Object[0]);
				ctx.req.setAttribute("csp.entry", entry);
			} catch (BlockException e) {
				ctx.respDone = true;
				ctx.resp.sendError(500); // 熔断了, 应该不是直接发个500
				return true;
			}
		}
		return true;
	}
	
	@Override
	public void postRoute(RouteContext ctx) throws IOException {
		AsyncEntry entry = (AsyncEntry) ctx.req.getAttribute("csp.entry");
		if (entry != null) {
			if (ctx.respFail) {
				entry.setError(new HttpResponseException("gateway.resp", null));
			}
			entry.exit(1);
		}
	}
}
