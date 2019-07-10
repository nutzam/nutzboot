package org.nutz.boot.starter.literpc.api;

import java.io.Serializable;

public class RpcResp implements Serializable {
    private static final long serialVersionUID = 1L;

    public Throwable err;
    public Object returnValue;

    protected long uuidMost, uuidLeast;

    public RpcResp() {}

    public RpcResp(Object returnValue) {
        this.returnValue = returnValue;
    }

    public RpcResp(Throwable err) {
        this.err = err;
    }

}
