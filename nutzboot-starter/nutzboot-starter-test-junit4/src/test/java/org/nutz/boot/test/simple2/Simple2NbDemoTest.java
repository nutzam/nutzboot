package org.nutz.boot.test.simple2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.boot.NbApp;
import org.nutz.boot.test.NbJUnit4Runner;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Lang;

/**
 * 单元测试示例B.
 * <p/>
 * 测试类不在在MainLauncher的package或子package
 * <p/>
 * 只使用ioc的注入服务,而且按类型注入.
 * <p/>
 * 
 * @author wendal
 *
 */
@RunWith(NbJUnit4Runner.class)
public class Simple2NbDemoTest extends Assert {

    // 测试注入
    @Inject
    public Simple2UserService users;

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

    // 测试类可提供public的static的createNbApp方法,用于定制当前测试类所需要的NbApp对象.
    // 测试类带@IocBean或不带@IocBean,本规则一样生效
    // 若不提供,默认使用当前测试类作为MainLauncher.
    // 也可以自定义NbJUnit4Runner, 继承NbJUnit4Runner并覆盖其createNbApp方法
    public static NbApp createNbApp() {
        return new NbApp().setMainClass(Simple2MainLauncher.class).setPrintProcDoc(false);
    }
}
