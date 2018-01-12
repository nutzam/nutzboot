package io.nutz.demo.simple;

import org.nutz.ioc.loader.annotation.IocBean;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;
import sun.java2d.pipe.SpanShapeRenderer;

import java.nio.ByteBuffer;

@IocBean
public class SimpleHandler implements ServerAioHandler {

    /**
     * 解码:将接受到的ByteBuffer对象解码成我们可是识别的业务包
     * 总的消息结构:消息头+消息体
     * 消息头结构: 4个字节,消息体的长度
     * 消息体结构: 数据json串的byte[]
     * @param buffer
     * @param channelContext
     * @return
     * @throws AioDecodeException
     */
    public Packet decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
        int limit = buffer.limit();//实际存储大小
        int position = buffer.position();//当前下标位置
        int realableLength = limit - position;
        //收到的数据组不了业务包,则返回null以告诉框架数据不够
        if(realableLength < SimplePacket.HEADER_LENGTH){
            return null;
        }

        //读取消息体的长度
        int bodyLength = buffer.getInt();
        if(bodyLength <0){
            throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
        }
        //计算本次需要的数据长度
        int needLength = SimplePacket.HEADER_LENGTH + bodyLength;
        //收到的数据是否足够组包
        int isDateEnough = realableLength - needLength;
        if(isDateEnough < 0){
            return null;
        }
        else{//组包成功
            SimplePacket pack = new SimplePacket();
            if(bodyLength > 0){
                byte[] dist = new byte[bodyLength];
                buffer.get(dist);
                pack.setBody(dist);
            }
            return pack;
        }
    }

    /**
     * 编码:将业务数据编码成可以发送的ByteBuffer
     * 总的消息结构: 消息头+ 消息体
     * 消息头结构:4个字节,存储消息体的长度
     * 消息体结构:业务json串的byte[]
     * @param packet
     * @param groupContext
     * @param channelContext
     * @return
     */
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
        SimplePacket helloPacket  = (SimplePacket) packet;
        byte[] body = helloPacket.getBody();
        int bodyLen = 0;
        if(body != null){
            bodyLen = body.length;
        }

        //Bytebuffer总长度 = 消息头+消息体的长度
        int allLen = SimplePacket.HEADER_LENGTH+bodyLen;
        //创建新的Bytebuffer
        ByteBuffer buf = ByteBuffer.allocate(allLen);
        //设置字节序
        buf.order(groupContext.getByteOrder());
        //写入消息头
        buf.putInt(bodyLen);
        //写入消息体
        if(body != null){
            buf.put(body);
        }
        return buf;
    }

    //处理消息
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        SimplePacket pack = (SimplePacket) packet;
        byte[] body = pack.getBody();
        if(body!=null){
            String s = new String(body, SimplePacket.CHARSET);
            System.out.println("接受到客户端消息:"+s);
            SimplePacket resp = new SimplePacket();
            resp.setBody(("我收到了您的消息:"+s).getBytes());
            Aio.send(channelContext,resp);
        }
    }
}
