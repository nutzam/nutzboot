package org.nutz.boot.starter.literpc.impl.endpoint.tcp;

public class LiteRpcTcpValues {
    public static final int HEADER_LENGHT = 4;
    public static final byte VERSION = 1;

    public static final byte OP_PING = 1;
    public static final byte OP_RPC_REQ = 2;
    public static final byte OP_RPC_RESP = 4;
}
