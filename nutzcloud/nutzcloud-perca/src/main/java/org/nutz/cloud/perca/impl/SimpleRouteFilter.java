package org.nutz.cloud.perca.impl;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;


/**
 * 固定服务器列表
 *
 */
public class SimpleRouteFilter extends AbstractUrlRewriteRouterFilter {

    protected static final Log log = Logs.get();

    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        super.setPropertiesProxy(ioc, conf, prefix);
        if (servers == null || servers.length == 0) {
        	throw new RuntimeException("simple router need services list!! prefix=" + prefix);
        }
    }

    public String getType() {
        return "simple";
    }

}
