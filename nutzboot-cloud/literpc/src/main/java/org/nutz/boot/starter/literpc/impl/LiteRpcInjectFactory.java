package org.nutz.boot.starter.literpc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.annotation.RpcInject;
import org.nutz.boot.starter.literpc.impl.proxy.AbstractRpcRefProxy;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocEventListener;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;

/**
 * 当一个ioc对象生成的时候,看看有无@RpcInject注解,如果有,注入代理对象
 *
 */
@IocBean(depose="depose")
public class LiteRpcInjectFactory implements IocEventListener {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected LiteRpc liteRpc;
    
    protected Map<String, AbstractRpcRefProxy> rpcProxys = new HashMap<>();
    
    protected ClassLoader classLoader = getClass().getClassLoader();
    
    public Object afterBorn(Object obj, String beanName) {
        Mirror<Object> mirror = Mirror.me(obj);
        Field[] fields = Mirror.me(obj).getFields();
        for (Field field : fields) {
            RpcInject rpcInject = field.getAnnotation(RpcInject.class);
            if (rpcInject == null)
                continue;
            AbstractRpcRefProxy proxy = Mirror.me(rpcInject.by()).born();
            proxy.setField(field);
            proxy.setIoc(ioc);
            proxy.setRpcInject(rpcInject);
            proxy.setObject(obj);
            proxy.setLiteRpc(liteRpc);
            Object t = Proxy.newProxyInstance(classLoader, new Class[] {field.getType()}, proxy);
            mirror.setValue(obj, field.getName(), t);
            proxy.afterInject();
            rpcProxys.put(field.toGenericString(), proxy);
        }
        return obj;
    }

    public Object afterCreate(Object obj, String beanName) {
        return obj;
    }

    public int getOrder() {
        return 0;
    }

    public void depose() {
        for (AbstractRpcRefProxy proxy : rpcProxys.values()) {
            proxy.beforeDepose();
        }
    }
}
