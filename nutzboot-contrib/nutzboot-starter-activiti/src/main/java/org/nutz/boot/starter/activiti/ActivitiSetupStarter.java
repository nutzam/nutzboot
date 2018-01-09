package org.nutz.boot.starter.activiti;

import org.activiti.engine.ProcessEngine;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ActivitiSetupStarter implements ServerFace {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    public void start() throws Exception {
        ioc.get(ProcessEngine.class); 
    }

    
}
