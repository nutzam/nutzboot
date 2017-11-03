package org.nutz.boot.env;

public interface EnvHolder {

    String get(String key);
    
    String get(String key, String defaultValue);
    
    String set(String key, String value);
}
