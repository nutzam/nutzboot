package org.nutz.boot.starter.literpc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.boot.starter.literpc.impl.RpcInvoker;
import org.nutz.boot.starter.literpc.impl.endpoint.RpcEndpoint;
import org.nutz.boot.starter.loach.client.LoachClient;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init")
public class LiteRpc implements LoachClient.UpdateListener {
    
    private static final Log log = Logs.get();
    
    protected static String RPC_REG_KEY = "literpc.v1";
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    /**
     * 持有所有的执行器
     */
    protected Map<String, RpcInvoker> invokers = new ConcurrentHashMap<>();
    
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
    
    public RpcInvoker getInvoker(String methodSign) {
        return invokers.get(methodSign);
    }
    
    public RpcInvoker registerInovker(String methodSign, RpcInvoker invoker) {
        return invokers.put(methodSign, invoker);
    }
    
    public static String getMethodSign(Method method) {
        return Lang.sha1(method.toGenericString());
    }
    
    public void updateLoachRegInfo() {
        LoachClient.EXT_REG_DATA.put(RPC_REG_KEY, new ArrayList<>(invokers.keySet()));
    }
    
    public List<NutMap> getServers(String methodSign) {
        return this.services.get(methodSign);
    }

    public void onUpdate(Map<String, List<NutMap>> services) {
        if (services == null || services.isEmpty()) {
            return; // fuck
        }
        Map<String, List<NutMap>> rpcMap = new HashMap<>();
        for (List<NutMap> _se : services.values()) {
            for (NutMap server : _se) {
                List<String> rpcKeys = server.getList(RPC_REG_KEY, String.class);
                if (rpcKeys == null || rpcKeys.isEmpty())
                    continue;
                for (String rpcKey : rpcKeys) {
                    List<NutMap> servers = rpcMap.get(rpcKey);
                    if (servers == null) {
                        servers = new ArrayList<>();
                        rpcMap.put(rpcKey, servers);
                    }
                    servers.add(server);
                }
            }
        }
        this.services = rpcMap;
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
