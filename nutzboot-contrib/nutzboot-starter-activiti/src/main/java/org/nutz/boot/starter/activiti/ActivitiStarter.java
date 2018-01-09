package org.nutz.boot.starter.activiti;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.activiti.ActivitiIocLoader;
import org.nutz.ioc.IocLoader;

public class ActivitiStarter implements IocLoaderProvider {

    public IocLoader getIocLoader() {
        return new ActivitiIocLoader();
    }
    
}
