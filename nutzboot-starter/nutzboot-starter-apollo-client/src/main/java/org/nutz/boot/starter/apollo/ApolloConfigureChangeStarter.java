package org.nutz.boot.starter.apollo;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

@IocBean
public class ApolloConfigureChangeStarter implements ServerFace, ConfigChangeListener {

    @Inject
    protected AppContext appContext;

    public void start() throws Exception {
        Config config = ConfigService.getAppConfig();
        appContext.getBeans(ConfigChangeListener.class).forEach((listener) -> config.addChangeListener(listener));
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        PropertiesProxy conf = appContext.getConf();
        for (String key : changeEvent.changedKeys()) {
            ConfigChange cc = changeEvent.getChange(key);
            switch (cc.getChangeType()) {
            case ADDED:
                conf.put(key, cc.getNewValue());
                break;
            case MODIFIED:
                conf.put(key, cc.getNewValue());
                break;
            case DELETED:
                conf.remove(key);
                break;
            default:
                break;
            }
        }
    }

}
