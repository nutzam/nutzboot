package io.nutz.cloud.demo;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.cloud.demo.bean.User;

@IocBean(create = "init")
public class CloudServiceLauncher {

    @Inject
    protected Dao dao;

    @Ok("raw")
    @At("/Status")
    public NutMap status() {
        return new NutMap();
    }
    
    public void init() {
        dao.create(User.class, false);
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
