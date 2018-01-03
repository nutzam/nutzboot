package org.nutz.boot.starter.nutz.mvc.api;

import java.util.Set;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.EntryDeterminer;
import org.nutz.mvc.impl.Loadings;

public interface ActionLoaderFace {

    default void getActions(Ioc ioc, Class<?> mainModule, EntryDeterminer determiner, Set<Class<?>> modules) {
        Loadings.scanModuleInPackage(modules, getClass().getPackage().getName());
    }
}
