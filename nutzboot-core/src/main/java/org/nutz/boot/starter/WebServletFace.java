package org.nutz.boot.starter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

public interface WebServletFace {

    String getName();

    String getPathSpec();

    default String[] getPathSpecs() {
        return new String[]{getPathSpec()};
    }

    Servlet getServlet();

    default Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    default void setServletContext(ServletContext sc) {

    }

    default boolean isAsyncSupported() {
        return false;
    }

    default MultipartConfigElement getMultipartConfig() {
        return null;
    }

    default int getLoadOnStartup() {
        return 1;
    }
}
