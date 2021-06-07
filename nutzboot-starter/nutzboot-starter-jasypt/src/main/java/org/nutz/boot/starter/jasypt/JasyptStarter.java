package org.nutz.boot.starter.jasypt;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init")
public class JasyptStarter {

    private static final Log log = Logs.get();

    public void init() {
        //TODO 初始化
    }

}
