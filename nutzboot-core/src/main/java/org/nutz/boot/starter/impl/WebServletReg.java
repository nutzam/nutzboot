package org.nutz.boot.starter.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

import org.nutz.boot.starter.WebServletFace;

public class WebServletReg implements WebServletFace {

    protected String name;

    protected String pathSpec;

    protected Servlet servlet;

    protected Map<String, String> initParameters = new HashMap<>();
    
    protected boolean asyncSupported;
    
    protected MultipartConfigElement multipartConfig;
    
    protected int loadOnStartup = 1;
    
    public WebServletReg() {
    }
    
    public WebServletReg(String name, Servlet servlet, String pathSpec) {
        this.name = name;
        this.servlet = servlet;
        this.pathSpec = pathSpec;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathSpec() {
        return pathSpec;
    }

    public void setPathSpec(String pathSpec) {
        this.pathSpec = pathSpec;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }
    
    public void addInitParameters(String key, String value) {
        initParameters.put(key, value);
    }

    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public MultipartConfigElement getMultipartConfig() {
        return multipartConfig;
    }

    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.multipartConfig = multipartConfig;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }
}
