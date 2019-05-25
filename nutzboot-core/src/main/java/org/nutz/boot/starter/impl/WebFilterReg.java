package org.nutz.boot.starter.impl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;

/**
 * 写个@IocBean工厂方法, 配置并返回本对象, 就可以了
 *
 */
public class WebFilterReg implements WebFilterFace {

    protected String name;

    protected String[] pathSpecs;

    protected Set<DispatcherType> dispatcheTypes = new HashSet<>();

    protected Filter filter;

    protected Map<String, String> initParameters = new HashMap<>();

    protected int order;

    public WebFilterReg() {
        dispatcheTypes.add(DispatcherType.REQUEST);
    }

    public WebFilterReg(String name, Filter filter, String pathSpec) {
        this();
        this.name = name;
        this.filter = filter;
        this.pathSpecs = new String[]{pathSpec};
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPathSpecs() {
        return pathSpecs;
    }

    public void setPathSpecs(String[] pathSpecs) {
        this.pathSpecs = pathSpecs;
    }

    public void setDispatcheTypes(Set<DispatcherType> dispatcheTypes) {
        this.dispatcheTypes = dispatcheTypes;
    }

    public Set<DispatcherType> getDispatcheTypes() {
        return dispatcheTypes;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.copyOf(dispatcheTypes);
    }

    public void addInitParameters(String key, String value) {
        initParameters.put(key, value);
    }

    @Override
    public String getPathSpec() {
        return pathSpecs == null || pathSpecs.length == 0 ? null : pathSpecs[0];
    }
}
