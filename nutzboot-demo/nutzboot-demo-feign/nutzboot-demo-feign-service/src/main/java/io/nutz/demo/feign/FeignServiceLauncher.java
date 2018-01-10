package io.nutz.demo.feign;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.feign.bean.User;

@IocBean(create = "init")
public class FeignServiceLauncher {

    @Inject
    protected Dao dao;

    public void init() {
        dao.create(User.class, true);
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
