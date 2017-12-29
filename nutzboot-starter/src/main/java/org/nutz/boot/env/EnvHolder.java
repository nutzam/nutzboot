package org.nutz.boot.env;

/**
 * 环境信息加载器
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface EnvHolder {

    /**
     * 根据key获取一个配置
     */
    String get(String key);

    /**
     * 根据key获取一个配置,如果没有找到,返回默认值
     */
    String get(String key, String defaultValue);

    /**
     * 设置一个环境变量
     * 
     * @param key
     *            键
     * @param value
     *            值
     */
    String set(String key, String value);
}
