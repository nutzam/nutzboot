package org.nutz.boot.starter.urule;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class UruleSpringEnvStarter extends SpringWebContextProxy {

    public UruleSpringEnvStarter() {
        configLocation = "classpath:urule-spring-context.xml";
        selfName = "urule";
    }

}
