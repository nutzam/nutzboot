package io.nutz.demo.simple.action;

import org.nutz.boot.tools.AsSpringBean;
import org.nutz.ioc.loader.annotation.IocBean;

import com.bstek.urule.model.ExposeAction;

@AsSpringBean
@IocBean
public class UruleCustomerTest {

    @ExposeAction("方法1")
    public boolean evalTest(String username) {
        if (username == null) {
            return false;
        } else if (username.equals("张三")) {
            return true;
        }
        return false;
    }
}