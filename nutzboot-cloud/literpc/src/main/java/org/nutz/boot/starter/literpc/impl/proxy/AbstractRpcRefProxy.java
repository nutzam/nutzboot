package org.nutz.boot.starter.literpc.impl.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.annotation.RpcInject;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.boot.starter.literpc.impl.endpoint.RpcEndpoint;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;

public abstract class AbstractRpcRefProxy implements InvocationHandler {

    protected Field field;
    protected RpcInject rpcInect;
    protected Ioc ioc;
    protected Object object;
    protected LiteRpc liteRpc;
    
    protected RpcEndpoint endpoint;
    
    protected RpcSerializer serializer;

    public void setField(Field field) {
        this.field = field;
    }
    
    public void setRpcInject(RpcInject rpcInect) {
        this.rpcInect = rpcInect;
    }
    
    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }
    
    public void setObject(Object object) {
        this.object = object;
    }
    
    public void setLiteRpc(LiteRpc liteRpc) {
        this.liteRpc = liteRpc;
    }
    
    public void afterInject() {
        endpoint = liteRpc.getEndpoint(Strings.sBlank(rpcInect.endpointType(), "http"));
        serializer = liteRpc.getSerializer(Strings.sBlank(rpcInect.serializer(), "jdk"));
    }
    
    public void beforeDepose() {
    }
}
