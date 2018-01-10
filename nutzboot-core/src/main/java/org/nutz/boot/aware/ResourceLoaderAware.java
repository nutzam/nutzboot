package org.nutz.boot.aware;

import org.nutz.boot.resource.ResourceLoader;

public interface ResourceLoaderAware {

    void setResourceLoader(ResourceLoader resourceLoader);
}
