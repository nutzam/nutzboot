package org.nutz.boot.starter.gateway.server.impl;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.boot.starter.gateway.server.RouteContext;
import org.nutz.boot.starter.gateway.server.RouteFilter;
import org.nutz.boot.starter.loach.client.LoachClient;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SimpleRouteFilter implements RouteFilter {
    
    private static final Log log = Logs.get();

    protected String prefix;

    protected String[] hostnames;

    protected String serviceName;

    protected String[] servers;

    protected String[] uriPrefixs;

    protected boolean removePrefix;

    protected Pattern uriPattern;

    protected int connectTimeOut, sendTimeOut, readTimeOut;

    protected boolean corsEnable;
    
    protected LoachClient loachClient;

    public void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) {
        // 需要匹配的域名
        String hostnames = conf.get(prefix + ".hostnames");
        if (!Strings.isBlank(hostnames)) {
            this.hostnames = Strings.splitIgnoreBlank(hostnames, "(;|,)");
        }
        // 固定转发的服务器vip
        String servers = conf.get(prefix + ".servers");
        if (!Strings.isBlank(servers)) {
            this.servers = Strings.splitIgnoreBlank(servers, "(;|,)");
        }
        else {
            // 服务名
            serviceName = conf.get(prefix + ".serviceName");
            if (!Strings.isBlank(serviceName)) {
                loachClient = ioc.get(LoachClient.class);
            }
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
        
        if (servers != null) {
            ctx.targetHost = servers[R.random(0, servers.length)];
        }
        else {
            List<NutMap> services = loachClient.getService(serviceName);
            if (services.isEmpty()) {
                log.debugf("emtry server list for [%s]", serviceName);
                ctx.resp.sendError(500);
                return false; // 终止匹配
            }
            else {
                NutMap service = services.get(R.random(0, services.size() - 1));
                ctx.targetHost = service.getString("vip");
                ctx.targetPort = service.getInt("port");
            }
        }

        return RouteFilter.super.preRoute(ctx);
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
                        }
                        else {
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

    public String nickname() {
        return "simple";
    }

}
