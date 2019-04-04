package io.nutz.demo.simple.tio;

import java.util.ArrayList;
import java.util.Arrays;

import org.nutz.boot.starter.tio.websocket.annotation.TIOnAfterHandshake;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnBeforeText;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnBytes;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnClose;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnHandshake;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnMap;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnText;
import org.nutz.boot.starter.tio.websocket.annotation.TioController;
import org.nutz.boot.starter.tio.websocketbean.TioWebsocketRequest;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.common.WsResponse;


/**
 * SampleController
 */
@IocBean
@TioController
public class SampleController {
	
	private final static  Log log = Logs.get();
	
    private static String user = "hanmeimei";
    private static String token = "token12345";
    
    @Inject
    private ServerGroupContext webSocketServerGroupContext;
    
    @TIOnHandshake
    public void handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {
        Tio.bindToken(channelContext, token);
        Tio.bindUser(channelContext, user);
        log.infof("{%s} handshake in sample", user);
    }

    @TIOnAfterHandshake
    public void onAfterHandshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {
        log.infof("{%s} onAfterHandshake in sample", channelContext.userid);
    }

    @TIOnClose
    public void onClose(ChannelContext channelContext) {
        log.infof("{%s} onClose in sample", channelContext.userid);
    }

    @TIOnBeforeText
    public TioWebsocketRequest OnBeforeText(ChannelContext channelContext, String text) {
        if (text.equals("ping")) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add("hello");
            strings.add("world");
            return new TioWebsocketRequest("ping",strings);
        }
        return new TioWebsocketRequest("unknown",text);
    }

    @TIOnMap
    public void onMapDefault(ChannelContext channelContext, String event, String object) {
        log.infof("{%s}:{%s} onMapDefault in sample: {%s}", channelContext.userid, event, object);
        Tio.send(channelContext, WsResponse.fromText("another send method", "UTF-8"));
    }

    @TIOnMap("ping")
    public void onMap(ChannelContext channelContext, ArrayList<String> strings) {
        log.infof("{%s} onMapDefault in sample: {%s}", channelContext.userid, Arrays.toString(strings.toArray()));
        Tio.send(channelContext, WsResponse.fromText("another send method", "UTF-8"));
    }

    @TIOnText
    public void onText(ChannelContext channelContext, String text) {
        log.infof("{%s} OnText in sample: {%s}", channelContext.userid, text);
    }

    @TIOnBytes
    public void onBytes(ChannelContext channelContext, byte[] bytes) {
    	
        log.infof("{%s} onBytes in sample: {%s}", channelContext.userid, Arrays.toString(bytes));
    }

}
