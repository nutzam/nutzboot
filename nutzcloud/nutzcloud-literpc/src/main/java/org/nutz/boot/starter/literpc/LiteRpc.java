package org.nutz.boot.starter.literpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.boot.starter.literpc.api.RpcEndpoint;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.boot.starter.literpc.impl.RpcInvoker;
import org.nutz.boot.starter.literpc.impl.RpcObjectInvoker;
import org.nutz.boot.starter.loach.client.LoachClient;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init")
public class LiteRpc {
    
    private static final Log log = Logs.get();
    
    public static String RPC_REG_KEY = "literpc.v1";
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    /**
     * 持有所有的执行器
     */
    protected Map<String, RpcObjectInvoker> invokers = new ConcurrentHashMap<>();
    
    /**
     * 方法签名对应的可用服务器列表
     */
    protected Map<String, List<NutMap>> services = new HashMap<>();
    
    /**
     * 受支持的序列化器
     */
    protected Map<String, RpcSerializer> serializers = new HashMap<>();
    
    /**
     * 受支持的通信方式
     */
    protected Map<String, RpcEndpoint> endpoints = new HashMap<>();
    
    public void init() {
        // 获取所有RpcSerializer实例,并注册
        for(String name : ioc.getNamesByType(RpcSerializer.class)) {
            registerSerializer(ioc.get(RpcSerializer.class, name));
        }
        // 获取所有RpcEndpoint实例,并注册
        for(String name : ioc.getNamesByType(RpcEndpoint.class)) {
            registerEndpoint(ioc.get(RpcEndpoint.class, name));
        }
    }
    
    public RpcInvoker getInvoker(String klassName, String methodSign) {
        RpcObjectInvoker objectInvoker = invokers.get(klassName);
        if (objectInvoker != null)
            return objectInvoker.invokers.get(methodSign);
        return null;
    }
    
    public RpcObjectInvoker registerInovker(String klassName, RpcObjectInvoker objectInvoker) {
        return invokers.put(klassName, objectInvoker);
    }
    
    public static String getMethodSign(Method method) {
        return String.format("%s:%s", method.getName(), Lang.sha1(method.toGenericString()).substring(0, 8));
    }
    
    public void updateLoachRegInfo() {
        LoachClient.EXT_REG_DATA.put(RPC_REG_KEY, invokers);
    }
    
    public List<NutMap> getServers(String klassName, String methodSign) {
        return this.services.get(klassName + ":" + methodSign);
    }

    public void setServices(Map<String, List<NutMap>> services) {
        this.services = services;
    }
    
    public RpcSerializer getSerializer(String name) {
        return serializers.get(name);
    }
    
    public void registerSerializer(RpcSerializer serializer) {
        log.debug("add RpcSerializer name=" + serializer.getName());
        this.serializers.put(serializer.getName(), serializer);
    }
    
    public RpcEndpoint getEndpoint(String name) {
        return endpoints.get(name);
    }
    
    public void registerEndpoint(RpcEndpoint endpoint) {
        log.debug("add RpcEndpoint name=" + endpoint.getName());
        this.endpoints.put(endpoint.getName(), endpoint);
    }
}
