package org.nutz.boot.starter.literpc.impl.endpoint;

import java.io.ByteArrayOutputStream;

import org.nutz.boot.starter.literpc.RpcException;
import org.nutz.boot.starter.literpc.api.RpcReq;
import org.nutz.boot.starter.literpc.api.RpcResp;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

@IocBean
public class HttpRpcEndpoint implements RpcEndpoint {

    public static String METHOD_HEADER_NAME = "LiteRpc-Method";
    public static String SC_HEADER_NAME = "LiteRpc-Serializer";
    public static String ENDPOINT_URI = "/literpc/endpoint";

    public RpcResp send(RpcReq rpcReq, NutMap server, RpcSerializer serializer) {
        String vip = server.getString("vip");
        int port = server.getInt("port");
        String url = "http://" + vip + ":" + port + ENDPOINT_URI;
        Request req = Request.post(url);
        Header header = req.getHeader();
        header.clear();
        header.set("Content-Type", "LiteRpcBody");
        header.set(METHOD_HEADER_NAME, rpcReq.methodSign);
        header.set(SC_HEADER_NAME, "jdk"); // 先固定为jdk,以后再扩展吧

        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try {
            serializer.write(rpcReq.args, tmp);
        }
        catch (Exception e1) {
            return new RpcResp(e1);
        }
        req.setData(tmp.toByteArray());

        Sender sender = Sender.create(req);
        sender.setConnTimeout(rpcReq.connectTimeout);
        sender.setTimeout(rpcReq.timeout);
        Response resp;
        try {
            resp = sender.send();
            if (!resp.isOK()) {
                throw new RpcException("endpoint resp code=" + resp.getStatus());
            }
        }
        catch (Exception e) {
            return new RpcResp(e);
        }
        try {
            return (RpcResp) serializer.read(resp.getStream());
        }
        catch (Exception e) {
            return new RpcResp(e);
        }
    }

    public String getName() {
        return "http";
    }
}
