package org.nutz.boot.tools;

import java.util.Properties;

import org.nutz.ioc.impl.PropertiesProxy;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Spring Ioc与NutzBoot之间的配置信息桥
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class Nb2SpringPropertyPlaceholder extends PropertyPlaceholderConfigurer {

    /**
     * 配置信息,由spring ioc进行注入
     */
    protected PropertiesProxy conf;

    public Nb2SpringPropertyPlaceholder() {
        // 总是忽略未能解析/未填写的变量
        setIgnoreUnresolvablePlaceholders(true);
        // 优先级高一些
        setOrder(100);
    }

    /**
     * 如果conf里面有对应的值,返回值,否则再见
     */
    protected String resolvePlaceholder(String placeholder, Properties props) {
        if (conf.has(placeholder))
            return conf.get(placeholder);
        return super.resolvePlaceholder(placeholder, props);
    }

    /**
     * conf的setter,spring要求有
     * 
     * @param conf
     *            PropertiesProxy实例
     */
    public void setConf(PropertiesProxy conf) {
        this.conf = conf;
    }
}
