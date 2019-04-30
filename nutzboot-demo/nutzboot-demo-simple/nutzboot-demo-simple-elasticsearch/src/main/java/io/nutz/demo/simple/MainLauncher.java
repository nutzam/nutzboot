package io.nutz.demo.simple;

import io.nutz.demo.simple.bean.User;
import io.nutz.demo.simple.page.Pagination;
import io.nutz.demo.simple.service.UserService;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Map;

/**
 * Created by wizzer on 2018/6/15.
 */
@IocBean(create = "init")
public class MainLauncher {
    private final static Log log = Logs.get();
    @Inject
    private UserService userService;

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

    public void init() {
        User user1 = new User();
        user1.setId("0001");
        user1.setName("大鲨鱼");
        user1.setEmail("wizzer@qq.com");
        user1.setCreateAt(Times.getTS());
        User user2 = new User();
        user2.setId("0002");
        user2.setName("wizzer");
        user2.setEmail("wizzer.cn@gmail.com");
        user2.setCreateAt(Times.getTS());
        userService.createOrUpdateData(user1);//添加user1
        userService.createOrUpdateData(user2);//添加user2
        Map<String, Object> map = userService.getUser("0001");
        log.debug("user 0001:\r\n" + Json.toJson(map));
        Pagination pagination = userService.listPage(1, 10, "大", true, false, "", "");
        log.debug("pagination:\r\n" + Json.toJson(pagination));


    }
}
