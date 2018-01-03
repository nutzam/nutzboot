package org.nutz.boot.starter.hystrix;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@IocBean(name="$aop_hystrix_command")
public class HystrixCommandAopConfigure extends SimpleAopMaker<HystrixCommand> {

    public List<? extends MethodInterceptor> makeIt(HystrixCommand t, Method method, Ioc ioc) {
        try {
            return Arrays.asList(new HystrixCommandInterceptor(t, method));
        }
        catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(method.toString(), e);
        }
    }

}
