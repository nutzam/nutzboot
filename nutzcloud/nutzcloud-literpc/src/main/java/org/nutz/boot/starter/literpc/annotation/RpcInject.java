package org.nutz.boot.starter.literpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.boot.starter.literpc.impl.proxy.AbstractRpcRefProxy;
import org.nutz.boot.starter.literpc.impl.proxy.DefaultRpcInjectProxy;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface RpcInject {
    
    String name() default "";

    Class<? extends AbstractRpcRefProxy> by() default DefaultRpcInjectProxy.class;
    
    int connectTimeout() default -1;
    
    int timeout() default -1;
    
    String endpointType() default "";
    
    String serializer() default "";
}
