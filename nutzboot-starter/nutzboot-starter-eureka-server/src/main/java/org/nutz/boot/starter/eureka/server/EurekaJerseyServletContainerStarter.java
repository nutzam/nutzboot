package org.nutz.boot.starter.eureka.server;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.loader.annotation.IocBean;

import com.sun.jersey.spi.container.servlet.ServletContainer;

@IocBean
public class EurekaJerseyServletContainerStarter implements WebFilterFace {

    public String getName() {
        return "jersey";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST);
    }

    public Filter getFilter() {
        return new ServletContainer();
    }

    public Map<String, String> getInitParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("com.sun.jersey.config.property.WebPageContentRegex", "/(flex|images|js|css|jsp)/.*");
        params.put("com.sun.jersey.config.property.packages", "com.sun.jersey;com.netflix");
        return params;
    }

    public int getOrder() {
        return 5;
    }

}
