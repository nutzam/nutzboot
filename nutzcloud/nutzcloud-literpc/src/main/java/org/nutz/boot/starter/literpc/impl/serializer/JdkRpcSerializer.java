package org.nutz.boot.starter.literpc.impl.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.nutz.boot.starter.literpc.api.RpcSerializer;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * JDK原生序列化, 最基本的,同时也是兼容性最好的序列化器
 * @author wendal
 *
 */
@IocBean
public class JdkRpcSerializer implements RpcSerializer {

    public void write(Object obj, OutputStream out) throws  Exception {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(obj);
        oos.flush();
    }

    public Object read(InputStream ins) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(ins);
        return ois.readObject();
    }

    public String getName() {
        return "jdk";
    }
}
