package org.nutz.boot.starter.seata.aop.lock;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.boot.starter.seata.SeataHelper;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import io.seata.spring.annotation.GlobalLock;

@IocBean(name = "$aop_fescar_GlobalLock")
public class SeataLockAopMaker extends SimpleAopMaker<GlobalLock> {

    public List<? extends MethodInterceptor> makeIt(GlobalLock t, Method method, Ioc ioc) {
        if (SeataHelper.disableGlobalTransaction)
            return null;
        return Arrays.asList(new SeataLockInterceptor());
    }

    public String[] getName() {
        return new String[0];
    }

    public boolean has(String name) {
        return false;
    }
}
