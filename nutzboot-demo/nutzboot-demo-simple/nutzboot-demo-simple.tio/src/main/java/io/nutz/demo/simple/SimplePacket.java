package io.nutz.demo.simple;

import org.tio.core.intf.Packet;

public class SimplePacket extends Packet {
    public static final int HEADER_LENGTH = 4;
    public static final String CHARSET = "utf-8";

    private byte[] body;


    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
