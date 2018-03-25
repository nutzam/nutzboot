package org.nutz.boot.test.simple;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.boot.NbApp;
import org.nutz.boot.test.junit4.NbJUnit4Runner;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

/**
 * 单元测试示例A,这是推荐的写法.
 * <p/>
 * 测试类在MainLauncher所在package或子package,被包含在ioc注解扫描的范围内.
 * <p/>
 * 可使用ioc的全部特性: 生命周期/注入
 * <p/>
 * 请务必看看NbJUnit4Runner的源码!!
 * 
 * @author wendal
 *
 */
@IocBean(create = "init")
@RunWith(NbJUnit4Runner.class)
public class SimpleNbDemoTest extends Assert {

    // 测试注入
    @Inject
    public SimpleUserService users;

    public void init() {
        System.out.println("say hi");
    }

    @Test(timeout = 5000)
    public void test_up_and_down() {
        // do nothing, just wait 1s
        Lang.quiteSleep(1000);
    }

    @Test
    public void test_service_inject() {
        assertNotNull(users);
        // 空用户名/密码测试
        assertFalse(users.login("", "123456"));
        assertFalse(users.login("admin123", ""));
        // 错误用户名/密码测试
        assertFalse(users.login("Tadmin123", "123456"));
        assertFalse(users.login("admin123", "V123456"));
        // 正确密码的测试
        assertTrue(users.login("admin123", "123456"));
    }
    
    // 这是可选的
    // 测试类可提供public的static的createNbApp方法,用于定制当前测试类所需要的NbApp对象.
    // 测试类带@IocBean或不带@IocBean,本规则一样生效
    // 若不提供,默认使用当前测试类作为MainLauncher.
    // 也可以自定义NbJUnit4Runner, 继承NbJUnit4Runner并覆盖其createNbApp方法
    public static NbApp createNbApp() {
        return new NbApp().setMainClass(SimpleMainLauncher.class).setPrintProcDoc(false);
    }
}
