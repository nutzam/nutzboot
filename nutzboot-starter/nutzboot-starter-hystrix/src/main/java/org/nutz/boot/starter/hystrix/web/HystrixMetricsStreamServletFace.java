package org.nutz.boot.starter.hystrix.web;

import javax.servlet.Servlet;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@IocBean
public class HystrixMetricsStreamServletFace implements WebServletFace {

    public String getName() {
        return "HystrixMetricsStreamServlet";
    }

    public String getPathSpec() {
        return "/hystrix.stream";
    }

    public Servlet getServlet() {
        return new HystrixMetricsStreamServlet();
    }

}
