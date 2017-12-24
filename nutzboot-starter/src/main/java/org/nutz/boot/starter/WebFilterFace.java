package org.nutz.boot.starter;

import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

public interface WebFilterFace {

    String getName();
    
    String getPathSpec();
    
    EnumSet<DispatcherType> getDispatches();
    
    Filter getFilter();
    
    Map<String, String> getInitParameters();
    
    int getOrder();
    
    public static interface FilterOrder {
        // whale,druid,shiro,nutz
        int WhaleFilter = 10;
        int DruidFilter = 20;
        int ShiroFilter = 30;
        int NutFilter = 50;
    }
}
