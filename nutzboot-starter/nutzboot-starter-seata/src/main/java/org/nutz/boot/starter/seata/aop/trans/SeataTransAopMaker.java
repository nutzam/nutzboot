package org.nutz.boot.starter.seata.aop.trans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.boot.starter.seata.SeataHelper;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;

@IocBean(name="$aop_fescar_GlobalTransactional")
public class SeataTransAopMaker extends SimpleAopMaker<GlobalTransactional> {
    
    private static FailureHandler DFT = new DefaultFailureHandlerImpl();

    public List<? extends MethodInterceptor> makeIt(GlobalTransactional t, Method method, Ioc ioc) {
        if (SeataHelper.disableGlobalTransaction)
            return null;
        FailureHandler failureHandler = DFT;
        if (ioc.has("fescarFailureHandler")) {
            failureHandler = ioc.get(FailureHandler.class, "fescarFailureHandler");
        }
        return Arrays.asList(new SeataTransInterceptor(failureHandler, t, method));
    }

    public String[] getName() {
        return new String[0];
    }
    
    public boolean has(String name) {
        return false;
    }
}
