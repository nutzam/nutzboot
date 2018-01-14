package org.nutz.boot.starter.zkclient;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;

@IocBean
public class ApolloConfigureChangeStarter implements ServerFace {
    
    @Inject
    protected AppContext appContext;

    public void start() throws Exception {
        Config config = ConfigService.getAppConfig();
        appContext.getBeans(ConfigChangeListener.class).forEach((listener)->config.addChangeListener(listener));
    }

}
