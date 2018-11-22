package org.nutz.boot.starter.actuator;

import java.util.Set;

import org.nutz.boot.starter.actuator.module.MonitorModule;
import org.nutz.boot.starter.nutz.mvc.api.ActionLoaderFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.EntryDeterminer;

@IocBean
public class ActuatorStarter implements ActionLoaderFace {

    @Override
    public void getActions(Ioc ioc, Class<?> mainModule, EntryDeterminer determiner, Set<Class<?>> modules) {
        modules.add(MonitorModule.class);
    }
}
