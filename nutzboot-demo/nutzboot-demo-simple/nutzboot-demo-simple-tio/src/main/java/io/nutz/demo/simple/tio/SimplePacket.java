package io.nutz.demo.simple.tio;

import org.tio.core.intf.Packet;

public class SimplePacket extends Packet {
    
    private static final long serialVersionUID = 1L;
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
