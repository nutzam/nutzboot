package io.nutz.demo.simple;

import io.nutz.demo.simple.service.UserService;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Created by 王庆华 on 2021/8/28.
 */
@IocBean
public class MainLauncher {
    private final static Log log = Logs.get();

    @Inject
    private UserService userService;

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
