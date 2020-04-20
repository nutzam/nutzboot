package org.nutz.cloud.perca.impl;

import java.util.List;

import org.nutz.boot.starter.loach.client.LoachClient;
import org.nutz.cloud.perca.RouteContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 兼容最老的代码, 虽然不知道有没有人在用
 */
public class OldSimpleRouterFilter extends AbstractUrlRewriteRouterFilter {

    protected static final Log log = Logs.get();
    
    protected LoachClient loachClient;

    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        super.setPropertiesProxy(ioc, conf, prefix);
        if (!Strings.isBlank(serviceName)) {
        	loachClient = ioc.get(LoachClient.class);
        }
    }
    
    @Override
    protected boolean selectTargetServer(RouteContext ctx) {
    	if (servers != null)
    		ctx.targetHost = servers[R.random(0, servers.length)];
    	else {
    		List<NutMap> services = loachClient.getService(serviceName);
            if (services.isEmpty()) {
                return false; // 终止匹配
            }
            NutMap service = services.get(R.random(0, services.size() - 1));
            ctx.targetHost = service.getString("vip");
            ctx.targetPort = service.getInt("port");
    	}
    	return true;
    }

    public String getType() {
        return "simple";
    }
}
