package org.nutz.boot.tools;

import java.util.Properties;

import org.nutz.ioc.impl.PropertiesProxy;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class Nb2SpringPropertyPlaceholder extends PropertyPlaceholderConfigurer {

    protected PropertiesProxy conf;

    public Nb2SpringPropertyPlaceholder() {
        setIgnoreUnresolvablePlaceholders(true);
        setOrder(100);
    }

    protected String resolvePlaceholder(String placeholder, Properties props) {
        if (conf.has(placeholder))
            return conf.get(placeholder);
        return super.resolvePlaceholder(placeholder, props);
    }

    public void setConf(PropertiesProxy conf) {
        this.conf = conf;
    }
}
