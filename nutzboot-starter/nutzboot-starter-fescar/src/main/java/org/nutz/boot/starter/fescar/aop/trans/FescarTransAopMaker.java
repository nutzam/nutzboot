package org.nutz.boot.starter.fescar.aop.trans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.boot.starter.fescar.FescarHelper;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl;
import com.alibaba.fescar.tm.api.FailureHandler;

@IocBean(name="$aop_fescar_GlobalTransactional")
public class FescarTransAopMaker extends SimpleAopMaker<GlobalTransactional> {
    
    private static FailureHandler DFT = new DefaultFailureHandlerImpl();

    public List<? extends MethodInterceptor> makeIt(GlobalTransactional t, Method method, Ioc ioc) {
        if (FescarHelper.disableGlobalTransaction)
            return null;
        FailureHandler failureHandler = DFT;
        if (ioc.has("fescarFailureHandler")) {
            failureHandler = ioc.get(FailureHandler.class, "fescarFailureHandler");
        }
        return Arrays.asList(new FescarTransInterceptor(failureHandler, t, method));
    }

    public String[] getName() {
        return new String[0];
    }
    
    public boolean has(String name) {
        return false;
    }
}
