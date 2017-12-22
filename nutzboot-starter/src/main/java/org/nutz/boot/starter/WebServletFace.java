package org.nutz.boot.starter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

public interface WebServletFace {

    String getName();
    
    String getPathSpec();
    
    Servlet getServlet();
    
    default Map<String, String> getInitParameters() {
        return new HashMap<>();
    }
}
