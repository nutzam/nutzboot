package org.nutz.cloud.config;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.cloud.config.spi.ConfigureEventHandler;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class CloudConfigureChangeStarter implements ServerFace {
    
    @Inject
    protected AppContext appContext;

    public void start() throws Exception {
        appContext.getBeans(ConfigureEventHandler.class).forEach((listener)->CloudConfig.addListener(listener));
    }

}
