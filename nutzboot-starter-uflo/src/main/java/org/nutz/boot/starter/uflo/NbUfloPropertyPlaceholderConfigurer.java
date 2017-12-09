package org.nutz.boot.starter.uflo;

import java.util.Properties;

import org.nutz.boot.AppContext;
import org.nutz.ioc.impl.PropertiesProxy;

import com.bstek.uflo.UfloPropertyPlaceholderConfigurer;

public class NbUfloPropertyPlaceholderConfigurer extends UfloPropertyPlaceholderConfigurer {

    protected PropertiesProxy conf;
    
    protected String resolvePlaceholder(String placeholder, Properties props) {
        if (conf == null)
            conf = AppContext.getDefault().getConfigureLoader().get();
        if (conf.has(placeholder))
            return conf.get(placeholder);
        return super.resolvePlaceholder(placeholder, props);
    }
}
