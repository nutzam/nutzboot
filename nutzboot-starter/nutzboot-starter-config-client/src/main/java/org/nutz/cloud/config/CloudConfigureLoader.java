package org.nutz.cloud.config;

import java.io.IOException;

import org.nutz.boot.config.impl.PropertiesConfigureLoader;

public class CloudConfigureLoader extends PropertiesConfigureLoader {
    
    public CloudConfigureLoader() {
        CloudConfig.init();
    }

    protected void readPropertiesPath(String path) throws IOException {
        if (path.contains("/") || path.contains("\\"))
            super.readPropertiesPath(path);
        else {
            CloudConfig.fromRemote(conf, path);
        }
    }

}
