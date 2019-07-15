package org.nutz.cloud.perca;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
@SuppressWarnings("serial")
public class PercaServlet extends AsyncMiddleManServlet implements WebServletFace {
    
    public static final String NAME_ROUTE_CONCEXT = "gateway.route_context";
    
    @Inject
    protected RouteConfig routeConfig;

    @Override
    protected void service(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) throws ServletException, IOException {
        RouteContext ctx = new RouteContext();
        ctx.setup(clientRequest, proxyResponse);
        clientRequest.setAttribute(NAME_ROUTE_CONCEXT, ctx);
        Iterator<RouteFilter> it = routeConfig.getRouteFilters();
        while (it.hasNext()) {
            if (!it.next().preRoute(ctx)) {
                return;
            }
        }
        if (ctx.targetHost == null && ctx.rewritedTarget == null) {
            proxyResponse.sendError(404);
            return;
        }
        super.service(clientRequest, proxyResponse);
    }
    
    @Override
    protected String rewriteTarget(HttpServletRequest clientRequest) {
        RouteContext ctx = ((RouteContext)clientRequest.getAttribute(NAME_ROUTE_CONCEXT));
        if (ctx.rewritedTarget != null)
            return ctx.rewritedTarget;
        String url = ctx.targetHost;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            
        }
        else {
            url = "http://" + url;
        }
        if (ctx.targetPort > 0) {
            url += ":" + ctx.targetPort;
        }
        if (ctx.targetUri == null) {
            url += ctx.uri;
        }
        else {
            url += ctx.targetUri;
        }
        if (ctx.queryString != null) {
            url += "?" + ctx.queryString;
        }
        return url;
    }

    public String getName() {
        return "gateway";
    }

    public String getPathSpec() {
        return "/*";
    }

    public Servlet getServlet() {
        return this;
    }
}
