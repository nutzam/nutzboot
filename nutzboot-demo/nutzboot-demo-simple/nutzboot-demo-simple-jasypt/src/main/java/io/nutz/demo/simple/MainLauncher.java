package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;


@IocBean(create = "init")
public class MainLauncher {


    @Inject
    protected PropertiesProxy conf;

    public void init() {
        System.out.println("test enc");
        System.out.println(conf.get("spring.datasource.username"));
    }

    // 默认配置的数据库是h2database,而且用了内存模式, 每次启动都是新的空白数据库
    // 配置其他数据库时,请务必加上对应的驱动程序, h2的依赖可删除
    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
