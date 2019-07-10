package org.nutz.boot.starter.literpc.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface RpcSerializer {

    void write(Object obj, OutputStream out) throws Exception;
    
    Object read(InputStream ins) throws Exception;
    
    String getName();
}
