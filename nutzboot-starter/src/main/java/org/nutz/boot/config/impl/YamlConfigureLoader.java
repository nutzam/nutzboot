package org.nutz.boot.config.impl;

import java.io.InputStream;

import org.nutz.ioc.impl.PropertiesProxy;

public class YamlConfigureLoader extends AbstractConfigureLoader {

    public void init() throws Exception {
        String path = envHolder.get("nutz.boot.configure.properties_path", "application.yml");
        try (InputStream ins = resourceLoader.get(path)) {
            if (ins == null) {
                throw new RuntimeException("yaml not found : " + path);
            }
            conf = new PropertiesProxy();
            readYaml(ins);
        }
    }
    
    protected void readYaml(InputStream ins ) {
        
    }

    
}
