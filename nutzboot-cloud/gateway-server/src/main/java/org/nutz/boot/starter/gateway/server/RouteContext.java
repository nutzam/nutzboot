package org.nutz.boot.starter.gateway.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RouteContext {

    public String method;
    public String uri;
    public String host;
    public Map<String, String> headers;
    public String queryString;
    
    public String targetHost;
    public String targetUri;
    public int targetPort;
    
    public String rewritedTarget;
    
    public int connectTimeOut, sendTimeOut, readTimeOut;
    
    public HttpServletResponse resp;
    public HttpServletRequest req;
    
    public void setup(HttpServletRequest req, HttpServletResponse resp) {
        method = req.getMethod().toUpperCase();
        uri = req.getRequestURI();
        host = req.getHeader("Host");
        headers = new HashMap<>();
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, req.getHeader(headerName));
        }
        queryString = req.getQueryString();
        this.req = req;
        this.resp = resp;
    }
}
