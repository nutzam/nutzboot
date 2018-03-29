package org.nutz.boot.starter.literpc.impl.endpoint;

import org.nutz.boot.starter.literpc.api.RpcReq;
import org.nutz.boot.starter.literpc.api.RpcResp;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.lang.util.NutMap;

public interface RpcEndpoint {

    RpcResp send(RpcReq req, NutMap server, RpcSerializer serializer);
    
    String getName();
}
