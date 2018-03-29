package org.nutz.boot.starter.literpc.impl;

import java.lang.reflect.Method;

import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;

public class RpcInvoker {

    protected Object obj;
    
    protected FastMethod fastMethod;
    
    protected Method method;
    
    public void setObj(Object obj) {
        this.obj = obj;
    }
    
    public void setFastMethod(FastMethod fastMethod) {
        this.fastMethod = fastMethod;
    }
    
    public void setMethod(Method method) {
        this.method = method;
        setFastMethod(FastClassFactory.get(method));
    }
    
    public Object invoke(Object...args) throws Throwable {
        return fastMethod.invoke(obj, args);
    }
}
