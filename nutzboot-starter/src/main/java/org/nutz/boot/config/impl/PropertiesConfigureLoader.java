package org.nutz.boot.config.impl;

import java.io.InputStream;

import org.nutz.ioc.impl.PropertiesProxy;

public class PropertiesConfigureLoader extends AbstractConfigureLoader {
    
    protected PropertiesProxy conf;

    public void init() throws Exception {
        String path = envHolder.get("nutz.boot.configure.properties_path", "application.properties");
        try (InputStream ins = resourceLoader.get(path)) {
            if (ins == null) {
                throw new RuntimeException("properties not found : " + path);
            }
            conf = new PropertiesProxy(ins);
        }
    }
}
