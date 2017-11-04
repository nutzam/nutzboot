package org.nutz.boot.starter;

import javax.servlet.ServletContextListener;

public class ShiroEnv implements WebContextListenerFace {

    public ServletContextListener getServletContextListener() {
        return new org.apache.shiro.web.env.EnvironmentLoaderListener();
    }
}
