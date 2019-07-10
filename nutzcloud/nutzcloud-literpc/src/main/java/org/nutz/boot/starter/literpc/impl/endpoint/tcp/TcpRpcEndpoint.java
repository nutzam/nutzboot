package org.nutz.boot.starter.literpc.impl.endpoint.tcp;

import static org.nutz.boot.starter.literpc.impl.endpoint.tcp.LiteRpcTcpValues.OP_PING;
import static org.nutz.boot.starter.literpc.impl.endpoint.tcp.LiteRpcTcpValues.OP_RPC_REQ;
import static org.nutz.boot.starter.literpc.impl.endpoint.tcp.LiteRpcTcpValues.OP_RPC_RESP;
import static org.nutz.boot.starter.literpc.impl.endpoint.tcp.LiteRpcTcpValues.VERSION;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.nutz.boot.starter.literpc.RpcException;
import org.nutz.boot.starter.literpc.api.RpcEndpoint;
import org.nutz.boot.starter.literpc.api.RpcReq;
import org.nutz.boot.starter.literpc.api.RpcResp;
import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create = "init")
public class TcpRpcEndpoint implements RpcEndpoint {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    protected Map<String, Socket> clients = new HashMap<>();

    protected byte[] lock = new byte[0];

    protected GenericKeyedObjectPool<SocketAddress, SocketHolder> pool;

    protected boolean debug;
    
    public static byte[] PKG_PING;
    static {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.putInt(2);
        buffer.put(VERSION);
        buffer.put(OP_PING);
        PKG_PING = buffer.array();
    }

    public void init() {
        debug = conf.getBoolean("literpc.endpoint.tcp.debug", false);
        GenericKeyedObjectPoolConfig poolConfig = new GenericKeyedObjectPoolConfig();
        poolConfig.setMaxTotal(500);
        poolConfig.setTestWhileIdle(true);
        pool = new GenericKeyedObjectPool<>(new RpcSocketFactory(), poolConfig);
    }

    public RpcResp send(RpcReq req, NutMap server, RpcSerializer serializer) {
        // 发送的格式
        // 4byte 长度数据
        // 1byte 版本数据
        // 1byte 请求类型,如果是PING请求,后面什么都没有了
        // 16byte UUID
        // UTF字符串 类名
        // UTF字符串 方法签名
        // UTF字符串 序列化器的名称
        // byte[] 序列化后的参数,供序列化器读取
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            DataOutputStream dos = new DataOutputStream(out);
            UUID uuid = UUID.randomUUID();
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
            dos.writeUTF(req.klass.getName());
            dos.writeUTF(req.methodSign);
            dos.writeUTF(serializer.getName());
            serializer.write(req.args, dos);
        }
        catch (Exception e) {
            return new RpcResp(e);
        }
        byte[] body = out.toByteArray();
        if (debug)
            log.debug("send " + Lang.fixedHexString(body));
        SocketHolder holder;
        InetSocketAddress addr = new InetSocketAddress(server.getString("vip"), server.getInt("port"));
        try {
            holder = pool.borrowObject(addr, 1000);
        }
        catch (Throwable e) {
            return new RpcResp(e);
        }
        try {
            DataOutputStream dos = holder.dos;
            dos.writeInt(body.length + 2);
            dos.write(VERSION);
            dos.write(OP_RPC_REQ);
            dos.write(body);
            dos.flush();

            int size = holder.dis.readInt(); // skip bodySize
            byte version = (byte) holder.dis.read();// skip VERSION
            if (debug)
                log.debug("version="+version);
            byte opType = holder.dis.readByte();
            if (opType != OP_RPC_RESP) {
                holder.socket.close();
                return new RpcResp(new RpcException("bad opType=" + opType));
            }
            // 读取UUID
            holder.dis.readLong();
            holder.dis.readLong();
            int respType = holder.dis.read();
            body = new byte[size - 2 - 1 - 16];
            holder.dis.readFully(body, 0, body.length);
            if (debug)
                log.debug("read " + Lang.fixedHexString(body));
            switch (respType) {
            case 0:
                return new RpcResp();
            case 1:
                return new RpcResp(serializer.read(new ByteArrayInputStream(body)));
            case 2:
            default:
                return new RpcResp((Throwable) serializer.read(new ByteArrayInputStream(body)));
            }
        }
        catch (Throwable e) {
            return new RpcResp(e);
        }
        finally {
            if (holder != null)
                pool.returnObject(addr, holder);
        }
    }

    public String getName() {
        return "tcp";
    }

    public class SocketHolder {
        protected Socket socket;
        protected DataInputStream dis;
        protected DataOutputStream dos;
    }

    public class RpcSocketFactory implements KeyedPooledObjectFactory<SocketAddress, SocketHolder> {

        @SuppressWarnings("resource")
		@Override
        public PooledObject<SocketHolder> makeObject(SocketAddress addr) throws Exception {
            SocketHolder socketHolder = new SocketHolder();
            Socket socket = new Socket();
            socket.connect(addr, 1000);
            socketHolder.socket = socket;
            socketHolder.dis = new DataInputStream(socket.getInputStream());
            socketHolder.dos = new DataOutputStream(socket.getOutputStream());
            return new DefaultPooledObject<TcpRpcEndpoint.SocketHolder>(socketHolder);
        }

        public void destroyObject(SocketAddress key, PooledObject<SocketHolder> p) throws Exception {
            Streams.safeClose(p.getObject().socket);
        }

        @Override
        public boolean validateObject(SocketAddress key, PooledObject<SocketHolder> p) {
            try {
                OutputStream out = p.getObject().socket.getOutputStream();
                out.write(PKG_PING);
                out.flush();
            }
            catch (Throwable e) {
                return false;
            }
            return false;
        }

        @Override
        public void activateObject(SocketAddress key, PooledObject<SocketHolder> p) throws Exception {}

        @Override
        public void passivateObject(SocketAddress key, PooledObject<SocketHolder> p) throws Exception {}

    }
}
