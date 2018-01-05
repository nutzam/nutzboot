package org.nutz.boot.starter.eureka.server;

import java.util.EventListener;

import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.ioc.loader.annotation.IocBean;

import com.netflix.eureka.EurekaBootStrap;

@IocBean
public class EurekaBootStrapStarter implements WebEventListenerFace {

    public EventListener getEventListener() {
        return new EurekaBootStrap();
    }

}
