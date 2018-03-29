package org.nutz.boot.starter.literpc.impl.endpoint;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.api.RpcResp;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.boot.starter.literpc.impl.RpcInvoker;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="_init")
public class HttpServletRpcEndpoint implements WebFilterFace, Filter {
    
    private static final Log log = Logs.get();
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    protected LiteRpc liteRpc;
    
    protected boolean debug;
    
    public void _init() {
        debug = conf.getBoolean("literpc.endpoint.http.debug", false);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        // 首先,检查是否有LiteRpc特有的header
        String methodSign = req.getHeader(HttpRpcEndpoint.METHOD_HEADER_NAME);
        if (Strings.isBlank(methodSign)) {
            if (debug)
                log.debug("miss http header " + HttpRpcEndpoint.METHOD_HEADER_NAME);
            resp.sendError(400);
            return;
        }
        String scName = req.getHeader(HttpRpcEndpoint.SC_HEADER_NAME);
        if (Strings.isBlank(scName)) {
            if (debug)
                log.debug("miss http header " + HttpRpcEndpoint.SC_HEADER_NAME);
            resp.sendError(400);
            return;
        }
        RpcSerializer serializer = liteRpc.getSerializer(scName);
        if (serializer == null) {
            if (debug)
                log.debug("not support serializer=" + scName);
            resp.sendError(400);
            return;
        }
        // 本服务器是否支持这个method
        RpcInvoker invoker = liteRpc.getInvoker(methodSign);
        if (invoker == null) {
            resp.setHeader("LiteRpc-Msg", "No such Method at this service methodSign="+methodSign);
            if (debug)
                log.debug("no such method methodSign=" + methodSign);
            resp.sendError(404);
            return;
        }
        // 反序列化方法参数
        Object[] args;
        try {
            args = (Object[]) serializer.read(req.getInputStream());
        }
        catch (Throwable e) {
            resp.setHeader("LiteRpc-Msg", "Serializer Exception when reading");
            if (debug)
                log.debug("Serializer Exception when reading", e);
            resp.sendError(404);
            return;
        }
        // 执行之
        RpcResp rpcResp = new RpcResp();
        try {
            rpcResp.returnValue = invoker.invoke(args);
        }
        catch (Throwable e) {
            rpcResp.err = e;
        }
        // 将结果序列化
        try {
            serializer.write(rpcResp, resp.getOutputStream());
        }
        catch (Throwable e) {
            if (debug)
                log.debug("Serializer Exception when writing", e);
            if (resp.isCommitted()) {
                // nothing we can do
            }
            else {
                resp.reset();
                resp.setHeader("LiteRpc-Msg", "Serializer Exception when write");
                resp.sendError(404);
                return;
            }
        }
    }
    
    //-------------------------------------------------------------------------------

    public String getName() {
        return "literpc";
    }

    public String getPathSpec() {
        return HttpRpcEndpoint.ENDPOINT_URI;
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST);
    }

    public Filter getFilter() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public int getOrder() {
        return WebFilterFace.FilterOrder.NutFilter - 5;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

}
