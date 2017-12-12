package org.nutz.boot.starter.uflo;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class UfloSpringEnvStarter extends SpringWebContextProxy {

    public UfloSpringEnvStarter() {
        configLocation = "classpath:uflo-spring-context.xml";
        selfName = "uflo";
    }

}
