package org.nutz.boot.starter.ureport;

import java.util.List;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class UreportSpringEnvStarter extends SpringWebContextProxy {

    public UreportSpringEnvStarter() {
        configLocation = "classpath:ureport-spring-context.xml";
        selfName = "ureport";
    }

    @Override
    protected List<String> getSpringBeanNames() {
        List<String> names = super.getSpringBeanNames();
        names.remove(selfName + ".props");
        return names;
    }
}
