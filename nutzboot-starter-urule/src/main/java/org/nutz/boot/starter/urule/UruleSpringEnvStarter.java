package org.nutz.boot.starter.urule;

import java.util.List;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;

@IocBean(create="init")
public class UruleSpringEnvStarter extends SpringWebContextProxy {
    
    @Inject
    protected PropertiesProxy conf;

    public UruleSpringEnvStarter() {
        configLocation = "classpath:urule-spring-context.xml";
        selfName = "urule";
    }

    public void init() {
        if (conf.has("urule.repository.dir")) {
            String dir = conf.get("urule.repository.dir");
            dir = Files.createDirIfNoExists(dir).getAbsolutePath();
            conf.set("rule.repository.di", dir);
        }
    }

    @Override
    protected List<String> getSpringBeanNames() {
        List<String> names = super.getSpringBeanNames();
        names.remove(selfName + ".props");
        return names;
    }
}
