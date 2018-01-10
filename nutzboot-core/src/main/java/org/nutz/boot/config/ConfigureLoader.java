package org.nutz.boot.config;

import org.nutz.ioc.impl.PropertiesProxy;

/**
 * 配置信息加载器
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface ConfigureLoader {

    /**
     * 设置命令行参数
     */
    void setCommandLineProperties(boolean allow, String... args);

    /**
     * 获取配置信息实例
     */
    PropertiesProxy get();
}
