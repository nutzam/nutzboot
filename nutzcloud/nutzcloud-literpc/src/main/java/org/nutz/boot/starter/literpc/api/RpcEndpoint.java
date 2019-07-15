package org.nutz.boot.starter.literpc.api;

import org.nutz.lang.util.NutMap;

public interface RpcEndpoint {

    RpcResp send(RpcReq req, NutMap server, RpcSerializer serializer);
    
    String getName();
}
