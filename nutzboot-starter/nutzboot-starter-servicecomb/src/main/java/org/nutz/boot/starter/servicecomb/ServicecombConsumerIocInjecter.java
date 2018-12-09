package org.nutz.boot.starter.servicecomb;

import java.lang.reflect.Field;

import org.apache.servicecomb.provider.pojo.Invoker;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.nutz.ioc.IocEventListener;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;

@IocBean
public class ServicecombConsumerIocInjecter implements IocEventListener {

    @Override
    public Object afterBorn(Object obj, String beanName) {
        if (obj != null) {
            for (Field field : obj.getClass().getDeclaredFields()) {
                RpcReference ref = field.getAnnotation(RpcReference.class);
                if (ref != null) {
                    Mirror.me(obj).setValue(obj, field, Invoker.createProxy(ref.microserviceName(), ref.schemaId(), field.getType()));
                }
            }
        }
        return obj;
    }

    @Override
    public Object afterCreate(Object obj, String beanName) {
        return obj;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
