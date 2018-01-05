package org.nutz.boot.starter.eureka.server;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.loader.annotation.IocBean;

import com.netflix.eureka.StatusFilter;

@IocBean
public class EurekaStatusFilterStarter implements WebFilterFace {

    public String getName() {
        return "statusFilter";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST);
    }

    public Filter getFilter() {
        return new StatusFilter();
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public int getOrder() {
        return 0;
    }

}
