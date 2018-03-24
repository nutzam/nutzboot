package org.nutz.boot.test.simple;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

@RunWith(NbTestRunner.class)
@IocBean
public class SimpleNbDemoTest extends Assert {
    
    // 测试注入
    @Inject
    public SimpleUserService users;

    @Test(timeout=5000)
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
}
