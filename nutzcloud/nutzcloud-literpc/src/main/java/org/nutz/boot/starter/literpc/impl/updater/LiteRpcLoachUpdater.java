package org.nutz.boot.starter.literpc.impl.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.loach.client.LoachClient;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

@IocBean
public class LiteRpcLoachUpdater implements LoachClient.UpdateListener {
    
    @Inject
    protected LiteRpc liteRpc;

    @SuppressWarnings("unchecked")
    public void onUpdate(Map<String, List<NutMap>> services) {
        if (services == null || services.isEmpty()) {
            return; // fuck
        }
        Map<String, List<NutMap>> rpcMap = new HashMap<>();
        for (List<NutMap> _se : services.values()) {
            for (NutMap server : _se) {
                Map<String, List<String>> rpcKeys = (Map<String, List<String>>)server.get(LiteRpc.RPC_REG_KEY);
                if (rpcKeys == null || rpcKeys.isEmpty())
                    continue;
                for (Map.Entry<String, List<String>> en : rpcKeys.entrySet()) {
                    String klassName = en.getKey();
                    for (String methodSign : en.getValue()) {
                        methodSign = klassName + ":" + methodSign;
                        List<NutMap> servers = rpcMap.get(methodSign);
                        if (servers == null) {
                            servers = new ArrayList<>();
                            rpcMap.put(methodSign, servers);
                        }
                        servers.add(server);
                    }
                }
            }
        }
        liteRpc.setServices(rpcMap);
    }
}
