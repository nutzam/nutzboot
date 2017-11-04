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
}
