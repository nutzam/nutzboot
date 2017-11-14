package org.nutz.boot.starter.nutz.mvc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutFilter;

public class NutFilterStarter implements WebFilterFace {

    public String getName() {
        return "nutz";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    public Filter getFilter() {
        return new NutFilter();
    }

    public Map<String, String> getInitParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("modules", NbMainModule.class.getName());
        return params;
    }

    public void setIoc(Ioc ioc) {
        // TODO Auto-generated method stub
        
    }

}
