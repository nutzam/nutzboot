package org.nutz.boot.starter.sentinel.annotation;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

@IocBean
public class SentinelAnnotationAop extends SimpleAopMaker<SentinelResource> {

    public List<? extends MethodInterceptor> makeIt(SentinelResource t, Method method, Ioc ioc) {
        return Lang.list(new SentinelMethodInterceptor(t, method, ioc));
    }

    public String[] getName() {
        return new String[0];
    }

    public boolean has(String name) {
        return false;
    }
}
