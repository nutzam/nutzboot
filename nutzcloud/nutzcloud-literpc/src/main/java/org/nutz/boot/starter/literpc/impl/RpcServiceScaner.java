package org.nutz.boot.starter.literpc.impl;

import java.lang.reflect.Method;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.annotation.RpcService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create = "init")
public class RpcServiceScaner {

    private static final Log log = Logs.get();

    @Inject
    protected LiteRpc liteRpc;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        for (String beanName : ioc.getNames()) {
            if (Strings.isBlank(beanName))
                continue;
            try {
                Class<?> t = ioc.getType(beanName);
                if (t == null)
                    continue;
                Object obj = null;
                for (Class<?> klass : t.getInterfaces()) {
                    RpcService rpcService = klass.getAnnotation(RpcService.class);
                    if (rpcService == null)
                        continue;
                    if (obj == null)
                        obj = ioc.get(null, beanName);
                    if (log.isDebugEnabled())
                        log.debugf("add RPC Mapping [%s] -> [%s]", klass.getName(), obj.getClass().getName());
                    RpcObjectInvoker objectInvoker = new RpcObjectInvoker();
                    for (Method method : klass.getMethods()) {
                        RpcInvoker invoker = new RpcInvoker();
                        invoker.setObj(obj);
                        invoker.setMethod(method);
                        objectInvoker.invokers.put(LiteRpc.getMethodSign(method), invoker);
                    }
                    liteRpc.registerInovker(klass.getName(), objectInvoker);
                }
            }
            catch (Exception e) {
                log.info("bad rpc object? skiped", e);
            }
        }
        try {
            liteRpc.updateLoachRegInfo();
        }
        catch (Throwable e) {
            log.info("skip updateLoachRegInfo : " + e.getMessage());
        }
    }
}
