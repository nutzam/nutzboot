package io.nutz.demo.ssdb;

import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.*;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

@IocBean(create = "init")
public class MainLauncher {
    public static final Log log = Logs.get();
    @Inject
    private SSDB ssdb;

    public void init() {
        ssdb.set("name", "wendal").check(); // call check() to make sure resp is ok
        Response resp = ssdb.get("name");
        if (!resp.ok()) {
            //it's not ok
        } else {
            log.info("name=" + resp.asString());
        }
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
