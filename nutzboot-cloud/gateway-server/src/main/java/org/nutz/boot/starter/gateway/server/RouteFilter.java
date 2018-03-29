package org.nutz.boot.starter.gateway.server;

import java.io.IOException;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;

public interface RouteFilter {

    default boolean preRoute(RouteContext ctx) throws IOException {
        return true;
    }
    
    default void postRoute(RouteContext ctx) throws IOException {}
    
    default void setPropertiesProxy(Ioc ioc, PropertiesProxy conf, String prefix) {}
    
    String nickname();
}
