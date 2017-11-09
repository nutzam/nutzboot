package org.nutz.boot.starter;

import java.util.Map;

import javax.servlet.Servlet;

public interface WebServletFace {

    String getName();
    
    String getPathSpec();
    
    Servlet getServlet();
    
    Map<String, String> getInitParameters();
}
