package org.nutz.boot.env;

public class SystemPropertiesEnvHolder implements EnvHolder {

    @Override
    public String get(String key) {
        return System.getProperty(key, System.getenv(key));
    }

    @Override
    public String get(String key, String defaultValue) {
        String value = get(key);
        if (value == null)
            value = defaultValue;
        return value;
    }

    @Override
    public String set(String key, String value) {
        return System.setProperty(key, value);
    }

}
