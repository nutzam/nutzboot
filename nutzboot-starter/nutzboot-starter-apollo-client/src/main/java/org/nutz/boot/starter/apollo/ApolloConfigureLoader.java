package org.nutz.boot.starter.apollo;

import org.nutz.boot.config.impl.AbstractConfigureLoader;
import org.nutz.ioc.impl.PropertiesProxy;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;

public class ApolloConfigureLoader extends AbstractConfigureLoader {

    public void init() throws Exception {
        Config config = ConfigService.getAppConfig();
        conf = new PropertiesProxy();
        config.getPropertyNames().forEach((key) -> conf.put(key, config.getProperty(key, null)));
    }

}
