package org.nutz.boot.starter.hystrix.web;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.loader.annotation.IocBean;

import com.netflix.hystrix.contrib.requestservlet.HystrixRequestContextServletFilter;

@IocBean
public class HystrixRequestContextServletFilterFace implements WebFilterFace {

    public String getName() {
        return "HystrixRequestContextServletFilter";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST);
    }

    public Filter getFilter() {
        return new HystrixRequestContextServletFilter();
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public int getOrder() {
        return FilterOrder.HystrixRequestFilter;
    }

}
