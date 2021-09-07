package io.nutz.demo.simple.service;

import io.nutz.demo.simple.MainLauncher;
import io.nutz.demo.simple.bean.User;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.nutz.boot.NbApp;
import org.nutz.boot.test.junit4.NbJUnit4Runner;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.util.List;

@IocBean
@RunWith(NbJUnit4Runner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {
    Log log = Logs.get();

    @Inject
    private UserService userService;

    @Test
    public void test01_AddUser() {
        User user = new User();
        user.setId(R.UU32());
        user.setName("wendal");
        user.setEmail("wendal@gmail.com");
        user.setCreateAt(Times.getTS());
        try {
            Assert.assertTrue(userService.addUser(user));
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test02_batchInsert() {
        try {
            User user1 = new User();
            user1.setId("1001");
            user1.setName("wendal");
            user1.setEmail("wendal@gmail.com");
            user1.setCreateAt(Times.getTS());

            User user2 = new User();
            user2.setId("1002");
            user2.setName("wizzer");
            user2.setEmail("wizzer@gmail.com");
            user2.setCreateAt(Times.getTS());

            User user3 = new User();
            user3.setId("1003");
            user3.setName("Eggsblue");
            user3.setEmail("topcodermydream@gmail.com");
            user3.setCreateAt(Times.getTS());

            userService.batchInsert(Lang.list(user1, user2, user3));
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test03_fetchUser() {
        try {
            final User user = userService.fetchUser("1002");
            log.debug("user:" + user);
            Assert.assertNotNull(user);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test04_query() {
        try {
            final List<User> list = userService.query("wendal", "wendal@gmail.com");
            log.info("query:" + Json.toJson(list));
            Assert.assertNotNull(list);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test05_queryPage() {
        try {
            final List<User> list = userService.queryPage("wendal", 1, 10);
            log.info("queryPage:" + Json.toJson(list));
            Assert.assertNotNull(list);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test06_deleteUser() {
        try {
            Assert.assertTrue(userService.deleteUser("1003"));
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test07_updateUser() {
        try {
            User user = new User();
            user.setId("1001");
            user.setName("Eggsblue-1");
            user.setEmail("1719411461@qq.com");

            Assert.assertTrue(userService.updateUser(user));
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Test
    public void test08_batchDeleteUser() {
        try {
            userService.batchDeleteUser(Lang.array("1002", "1003"));
        } catch (IOException e) {
            log.error(e);
        }
    }

    @After
    public void after() {
        try {
            userService.deleteIndex();
        } catch (IOException e) {
            log.error(e);
        }
    }

    public static NbApp createNbApp() {
        NbApp nb = new NbApp().setMainClass(MainLauncher.class);
        nb.getAppContext().setMainPackage("io.nutz");
        return nb;
    }

}
