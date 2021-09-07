package org.nutz.boot.starter.servicecomb;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ServiceCombStarter implements ServerFace {

    public void start() throws Exception {
        BeanUtils.init();
    }
}
