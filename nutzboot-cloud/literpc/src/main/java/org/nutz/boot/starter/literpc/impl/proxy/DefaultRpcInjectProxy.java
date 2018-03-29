package org.nutz.boot.starter.literpc.impl.proxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.nutz.boot.starter.literpc.LiteRpc;
import org.nutz.boot.starter.literpc.RpcException;
import org.nutz.boot.starter.literpc.api.RpcReq;
import org.nutz.boot.starter.literpc.api.RpcResp;
import org.nutz.lang.util.NutMap;

public class DefaultRpcInjectProxy extends AbstractRpcRefProxy {

    protected AtomicLong AL = new AtomicLong();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建RpcReq
        RpcReq req = new RpcReq();
        req.args = args;
        req.object = proxy;
        req.connectTimeout = rpcInect.connectTimeout() == -1 ? 1000 : rpcInect.connectTimeout();
        req.timeout = rpcInect.timeout() == -1 ? 1000 : rpcInect.timeout();
        req.method = method;
        req.methodSign = LiteRpc.getMethodSign(method);
        // 获取支持该方法的服务器信息
        List<NutMap> servers = liteRpc.getServers(req.methodSign);
        if (servers == null || servers.isEmpty()) {
            throw new RpcException("No server support -> [" + method + "]");
        }
        // 选一个,执行之
        NutMap server = servers.get((int)(AL.incrementAndGet() % servers.size()));
        RpcResp resp = endpoint.send(req, server, serializer);
        if (resp.err == null)
            return resp.returnValue;
        throw resp.err;
    }
}
