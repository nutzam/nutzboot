package org.nutz.boot.starter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.integration.shiro.ShiroFilter2;

public class ShiroFilterStarter implements WebFilterFace {

    public String getName() {
        return "shiro";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR, DispatcherType.ASYNC);
    }

    public Filter getFilter() {
        return new ShiroFilter2();
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

}
