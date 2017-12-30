package org.nutz.boot.starter.caffeine;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name = "$aop_cache")
public class CaffeineStarter extends SimpleAopMaker<Cache> {
    
    public static String PRE = "caffeine.cache";

    private static List<? extends MethodInterceptor> interceptor;

    @Override
    public List<? extends MethodInterceptor> makeIt(Cache t, Method method, Ioc ioc) {
        if (interceptor == null) {
            synchronized (CaffeineStarter.class) {
                if (interceptor == null)
                    interceptor = Arrays.asList(ioc.get(CaffeineInterceptor.class));
            }
        }
        return interceptor;
    }

}