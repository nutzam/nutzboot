package org.nutz.boot.starter.literpc.impl;

import java.lang.reflect.Method;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.api.RpcService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean(create="init")
public class RpcServiceScaner {

    @Inject
    protected LiteRpc liteRpc;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    public void init() {
        for (String beanName : ioc.getNamesByType(RpcService.class)) {
            if (Strings.isBlank(beanName))
                continue;
            Object obj = ioc.get(null, beanName);
            for (Class<?> klass : obj.getClass().getInterfaces()) {
                if (klass == RpcService.class)
                    continue;
                if (RpcService.class.isAssignableFrom(klass)) {
                    for (Method method : klass.getMethods()) {
                        RpcInvoker invoker = new RpcInvoker();
                        invoker.setObj(obj);
                        invoker.setMethod(method);
                        liteRpc.registerInovker(LiteRpc.getMethodSign(method), invoker);
                    }
                }
            }
        }
        liteRpc.updateLoachRegInfo();
    }
}
