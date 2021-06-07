package org.nutz.cloud.perca;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
@SuppressWarnings("serial")
public class PercaServlet extends AsyncMiddleManServlet implements WebServletFace {
    
	private static final Log log = Logs.get();
	
    public static final String NAME_ROUTE_CONCEXT = "gateway.route_context";
    
    @Inject
    protected RouteConfig routeConfig;

    @Inject
    protected RequestContentTransformer requestContentTransformer;

    @Override
    protected void service(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) throws ServletException, IOException {
        RouteContext ctx = new RouteContext();
        ctx.setup(clientRequest, proxyResponse);
        clientRequest.setAttribute(NAME_ROUTE_CONCEXT, ctx);
        RouterMaster master = null;
        for (RouterMaster tmp : routeConfig.getRouteMasters()) {
			if (tmp.match(ctx)) {
				master = tmp;
				break;
			}
		}
        if (master != null) {
        	master.preRoute(ctx);
        	if (ctx.respDone) {
        		return;
        	}
        	super.service(clientRequest, proxyResponse);
        }
        else {
        	proxyResponse.sendError(404);
        }
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
    
    @Override
    public boolean isAsyncSupported() {
    	return true;
    }
    
    @Override
    protected void onProxyResponseFailure(HttpServletRequest clientRequest, HttpServletResponse proxyResponse,
    		Response serverResponse, Throwable failure) {
    	RouteContext ctx = (RouteContext) clientRequest.getAttribute(NAME_ROUTE_CONCEXT);
    	try {
    		ctx.respFail = true;
			ctx.rmaster.postRoute(ctx);
			if (ctx.respDone)
				return;
		} catch (IOException e) {
			log.error("bad postRoute", e);
		}
    	super.onProxyResponseFailure(clientRequest, proxyResponse, serverResponse, failure);
    }
    
    @Override
    protected void onProxyResponseSuccess(HttpServletRequest clientRequest, HttpServletResponse proxyResponse,
    		Response serverResponse) {
    	RouteContext ctx = (RouteContext) clientRequest.getAttribute(NAME_ROUTE_CONCEXT);
    	try {
    		ctx.respFail = false;
			ctx.rmaster.postRoute(ctx);
			if (ctx.respDone)
				return;
		} catch (IOException e) {
			log.error("bad postRoute", e);
		}
    	super.onProxyResponseSuccess(clientRequest, proxyResponse, serverResponse);
    }

    @Override
    protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
        requestContentTransformer.setProxyRequest(proxyRequest);
        return requestContentTransformer;
    }
}
