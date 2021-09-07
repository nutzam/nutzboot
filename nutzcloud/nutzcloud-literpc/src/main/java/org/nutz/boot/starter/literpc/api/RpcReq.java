package org.nutz.boot.starter.literpc.api;

import java.lang.reflect.Method;

public class RpcReq {
    public Class<?> klass;
    public Object object;
    public Method method;
    public String methodSign;
    public Object[] args;
    public int timeout, connectTimeout;
}
