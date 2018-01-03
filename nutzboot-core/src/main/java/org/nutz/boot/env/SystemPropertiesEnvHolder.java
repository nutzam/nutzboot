package org.nutz.boot.env;

/**
 * 环境信息加载器
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class SystemPropertiesEnvHolder implements EnvHolder {

    /**
     * System.properties获取配置信息,如果没有,就从System.env获取
     */
    public String get(String key) {
        return System.getProperty(key, System.getenv(key));
    }

    /**
     * 获取配置信息,没有的话,返回默认值
     */
    public String get(String key, String defaultValue) {
        String value = get(key);
        if (value == null)
            value = defaultValue;
        return value;
    }

    /**
     * 设置配置信息
     */
    public String set(String key, String value) {
        return System.setProperty(key, value);
    }

}
