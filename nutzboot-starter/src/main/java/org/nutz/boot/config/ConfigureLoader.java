package org.nutz.boot.config;

import org.nutz.ioc.impl.PropertiesProxy;

public interface ConfigureLoader {

	/**
	 * 设置命令行参数
	 */
	void setCommandLineProperties(String...args);
	
	/**
	 * 获取配置信息实例
	 */
    PropertiesProxy get();
}
