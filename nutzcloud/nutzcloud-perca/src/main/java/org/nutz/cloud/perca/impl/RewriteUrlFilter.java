package org.nutz.cloud.perca.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.nutz.cloud.perca.RouteContext;
import org.nutz.cloud.perca.RouteFilter;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wentao
 * @version 1.0.0
 * @title URL重定向过滤器
 * @description 将指定url重定向到指定服务
 * @package org.nutz.cloud.perca.impl
 * @create 2020-10-26 9:56 下午
 *
 */
public class RewriteUrlFilter implements RouteFilter, AutoCloseable {
    // 配置实例
    // gw.OrderRewrite.filters=rewrite-url
    // gw.OrderRewrite.uri=/bs/v1/orderSync
    // gw.OrderRewrite.targetUri=/order/bs/v1/orderSync
    // gw.OrderRewrite.serviceName=order

    private static final String URI = ".uri";
    private static final String TARGET_URI = ".targetUri";
    private static final String SERVICE_NAME = ".serviceName";

    /**
     * Naming Service
     */
    protected NamingService nacosNamingService;

    private String name = "";
    private String uri = "";
    private String targetUri = "";
    private String serviceName = "";

    /**
     * 日志对象
     */
    protected static final Log log = Logs.get();

    @Override
    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        this.name = prefix + "-RewriteFilter";
        this.uri = conf.get(prefix + URI);
        this.targetUri = conf.get(prefix + TARGET_URI);
        this.serviceName = conf.get(prefix + SERVICE_NAME);
        this.nacosNamingService = ioc.get(NamingService.class, "nacosNamingService");
    }

    @Override
    public boolean match(RouteContext ctx) {
        try {
            if (ctx.uri.equals(this.uri)) {
                Instance healthyInstance = this.nacosNamingService.selectOneHealthyInstance(this.serviceName);
                String oldTargetHost = ctx.targetHost;
                int oldTargetPort = ctx.targetPort;
                String oldUri = ctx.uri;
                ctx.targetHost = healthyInstance.getIp();
                ctx.targetPort = healthyInstance.getPort();
                ctx.uri = this.targetUri;
                log.debugf("转发请求[%s:%s%s] => [%s:%s%s]", oldTargetHost, oldTargetPort, oldUri, ctx.targetHost, ctx.targetPort, ctx.uri);
                return true;
            }
        } catch (NacosException e) {
            log.debug("selectOneHealthyInstance fail", e);
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "RewriteUrlFilter";
    }

    @Override
    public void close() {

    }
}
