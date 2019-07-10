package org.nutz.boot.starter.literpc.impl.endpoint.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.ProtocolHandlers;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.nutz.boot.starter.literpc.RpcException;
import org.nutz.boot.starter.literpc.api.RpcEndpoint;
import org.nutz.boot.starter.literpc.api.RpcReq;
import org.nutz.boot.starter.literpc.api.RpcResp;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

@IocBean(create = "init", depose = "depose")
public class HttpRpcEndpoint implements RpcEndpoint {

    public static String KLASS_HEADER_NAME = "LiteRpc-Klass";
    public static String METHOD_HEADER_NAME = "LiteRpc-Method";
    public static String SC_HEADER_NAME = "LiteRpc-Serializer";
    public static String ENDPOINT_URI = "/literpc/endpoint";
    protected static final String PRE = "literpc.endpoint.http.";
    @Inject
    protected PropertiesProxy conf;
    protected HttpClient client;
    protected Executor executor;
    
    public RpcResp send(RpcReq rpcReq, NutMap server, RpcSerializer serializer) {
        //return send_by_nutz_http(rpcReq, server, serializer);
        return send_by_jetty_client(rpcReq, server, serializer);
    }

    @SuppressWarnings("deprecation")
    public void init() throws Exception {
        client = new HttpClient(new SslContextFactory(true));
        client.setFollowRedirects(false);
        client.setCookieStore(new HttpCookieStore.Empty());

        executor = new QueuedThreadPool(conf.getInt(PRE + ".maxThreads", 256));
        client.setExecutor(executor);
        client.setMaxConnectionsPerDestination(conf.getInt(PRE + ".maxConnections", 256));
        client.setIdleTimeout(conf.getLong(PRE + ".idleTimeout", 30000));

        client.setConnectTimeout(conf.getLong(PRE + ".connectTime", 1000));

        if (conf.has(PRE + "requestBufferSize"))
            client.setRequestBufferSize(conf.getInt(PRE + "requestBufferSize"));

        if (conf.has(PRE + "responseBufferSize"))
            client.setResponseBufferSize(conf.getInt(PRE + "responseBufferSize"));

        client.start();

        // Content must not be decoded, otherwise the client gets confused.
        client.getContentDecoderFactories().clear();

        // Pass traffic to the client, only intercept what's necessary.
        ProtocolHandlers protocolHandlers = client.getProtocolHandlers();
        protocolHandlers.clear();
    }

    public void depose() throws Exception {
        if (client != null)
            client.stop();
    }

    public RpcResp send_by_jetty_client(RpcReq rpcReq, NutMap server, RpcSerializer serializer) {
        String vip = server.getString("vip");
        int port = server.getInt("port");
        String url = "http://" + vip + ":" + port + ENDPOINT_URI;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            serializer.write(rpcReq.args, bao);
        }
        catch (Throwable e1) {
            return new RpcResp(e1);
        }
        ContentResponse resp;
        try {
            resp = client.newRequest(url)
                    .method(HttpMethod.POST)
                    .header(KLASS_HEADER_NAME, rpcReq.klass.getName())
                    .header(METHOD_HEADER_NAME, rpcReq.methodSign)
                    .header(SC_HEADER_NAME, serializer.getName())
                    .content(new BytesContentProvider(bao.toByteArray()))
                    .send();
        }
        catch (InterruptedException | TimeoutException | ExecutionException e1) {
            return new RpcResp(e1);
        }
        if (resp.getStatus() != 200) {
            throw new RpcException("endpoint resp code=" + resp.getStatus());
        }
        try {
            return (RpcResp) serializer.read(new ByteArrayInputStream(resp.getContent()));
        }
        catch (Exception e) {
            return new RpcResp(e);
        }
    }

//    public RpcResp send_by_nutz_http(RpcReq rpcReq, NutMap server, RpcSerializer serializer) {
//        String vip = server.getString("vip");
//        int port = server.getInt("port");
//        String url = "http://" + vip + ":" + port + ENDPOINT_URI;
//        Request req = Request.post(url);
//        Header header = req.getHeader();
//        header.clear();
//        header.set("Content-Type", "LiteRpcBody");
//        header.set(METHOD_HEADER_NAME, rpcReq.methodSign);
//        header.set(SC_HEADER_NAME, serializer.getName());
//
//        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
//        try {
//            serializer.write(rpcReq.args, tmp);
//        }
//        catch (Exception e1) {
//            return new RpcResp(e1);
//        }
//        req.setData(tmp.toByteArray());
//
//        Sender sender = Sender.create(req);
//        sender.setConnTimeout(rpcReq.connectTimeout);
//        sender.setTimeout(rpcReq.timeout);
//        Response resp;
//        try {
//            resp = sender.send();
//            if (!resp.isOK()) {
//                throw new RpcException("endpoint resp code=" + resp.getStatus());
//            }
//        }
//        catch (Exception e) {
//            return new RpcResp(e);
//        }
//        try {
//            return (RpcResp) serializer.read(resp.getStream());
//        }
//        catch (Exception e) {
//            return new RpcResp(e);
//        }
//    }

    public String getName() {
        return "http";
    }
}
