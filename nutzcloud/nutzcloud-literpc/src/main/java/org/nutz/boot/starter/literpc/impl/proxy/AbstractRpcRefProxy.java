package org.nutz.boot.starter.literpc.impl.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.annotation.RpcInject;
import org.nutz.boot.starter.literpc.api.RpcEndpoint;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

public abstract class AbstractRpcRefProxy implements InvocationHandler {

    protected Field field;
    protected RpcInject rpcInect;
    protected Ioc ioc;
    protected Object object;
    protected LiteRpc liteRpc;
    protected Class<?> klass;
    protected PropertiesProxy conf;

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

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }
    
    public void setConf(PropertiesProxy conf) {
		this.conf = conf;
	}

    public void afterInject() {
        endpoint = liteRpc.getEndpoint(Strings.sBlank(rpcInect.endpointType(), conf.get("literpc.endpoint.type", "http")));
        serializer = liteRpc.getSerializer(Strings.sBlank(rpcInect.serializer(), conf.get("literpc.serializer.type", "jdk")));
    }

    public void beforeDepose() {}
}
