package io.nutz.demo.feign;

import java.util.List;

import org.nutz.boot.AppContext;
import org.nutz.boot.NbApp;
import org.nutz.boot.starter.feign.annotation.FeignInject;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.nutz.demo.feign.bean.User;
import io.nutz.demo.feign.service.UserService;

@IocBean(create = "init")
public class MainLauncher {

    private static final Log log = Logs.get();

    @Inject
    protected Dao dao;

    public void init() {
        dao.create(User.class, true);
    }

    //-------------------------------------------------------
    // 下面这段代码应该是另外一个项目中的,放在一起只是为了演示方便
    @FeignInject
    protected UserService userService;

    public void mock_client_call() {
        List<User> users = userService.list();
        log.info("users=" + Json.toJson(users));
        User haoqoo = userService.add("haoqoo", 19);
        User wendal = userService.add("wendal", 28);
        users = userService.list();
        log.info("users=" + Json.toJson(users));
        userService.delete(haoqoo.getId());
        userService.delete(wendal.getId());
        users = userService.list();
        log.info("users=" + Json.toJson(users));
    }
    //--------------------------------------------------------

    public static void main(String[] args) throws Exception {
        // new NbApp().setPrintProcDoc(true).run();
        NbApp app = new NbApp().setPrintProcDoc(true);
        app.start();
        Thread.sleep(5000); // 一般都能启动完成了吧?
        Ioc ioc = AppContext.getDefault().getIoc();
        MainLauncher launcher = ioc.get(MainLauncher.class);
        launcher.mock_client_call();
        app.shutdown();
    }

}
