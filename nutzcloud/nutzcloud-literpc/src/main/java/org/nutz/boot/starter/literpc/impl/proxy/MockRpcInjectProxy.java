package org.nutz.boot.starter.literpc.impl.proxy;

import java.lang.reflect.Method;

public class MockRpcInjectProxy extends AbstractRpcRefProxy {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

}
