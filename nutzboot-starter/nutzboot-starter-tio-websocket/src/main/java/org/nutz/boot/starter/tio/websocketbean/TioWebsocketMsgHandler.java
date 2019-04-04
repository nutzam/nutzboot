package org.nutz.boot.starter.tio.websocketbean;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.server.handler.IWsMsgHandler;


/**
 * TioWebsocket message handler
 */
public class TioWebsocketMsgHandler implements IWsMsgHandler {

	private final static Log log = Logs.get();
	
    private TioWebSocketMethods methods;

    
    public TioWebsocketMsgHandler(TioWebSocketMethods methods) {
		super();
		this.methods = methods;
	}

	/**
     * handshake
     * @param httpRequest tio-http-request
     * @param httpResponse tio-http-response
     * @param channelContext context
     * @return tio-http-response
     * @throws Exception e
     */
    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        String clientip = httpRequest.getClientIp();
        log.debugf("receive {}'s websocket handshake packet \r\n{}", clientip, httpRequest.toString());

        TioWebsocketMethodMapper handshake = methods.getHandshake();
        if (handshake != null) {
            handshake.getMethod().invoke(handshake.getInstance(), httpRequest, httpResponse, channelContext);
        }
        return httpResponse;
    }

    /**
     * afterHandshaked
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param channelContext channelContext
     * @throws Exception e
     */
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        log.debug("onAfterHandshaked");
        TioWebsocketMethodMapper onAfterHandshaked = methods.getOnAfterHandshaked();
        if (onAfterHandshaked != null) {
            onAfterHandshaked.getMethod().invoke(onAfterHandshaked.getInstance(), httpRequest, httpResponse, channelContext);
        }
    }

    /**
     * close connection
     * @param wsRequest wsRequest
     * @param bytes bytes
     * @param channelContext channelContext
     * @return AnyObject
     * @throws Exception e
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        TioWebsocketMethodMapper onClose = methods.getOnClose();
        if (onClose != null) {
            onClose.getMethod().invoke(onClose.getInstance(), channelContext);
        }
        log.debug("onClose");
        Tio.remove(channelContext, "onClose");
        return null;
    }

    /**
     * receive bytes
     * @param wsRequest wsRequest
     * @param bytes bytes
     * @param channelContext channelContext
     * @return anyObject
     * @throws Exception e
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        log.debug("onBytes");
        TioWebsocketMethodMapper onBytes = methods.getOnBytes();
        if (onBytes != null) {
            onBytes.getMethod().invoke(onBytes.getInstance(), channelContext, bytes);
        } else {
            TioWebsocketMethodMapper onBeforeBytes = methods.getOnBeforeBytes();
            if (onBeforeBytes != null) {
                TioWebsocketRequest invoke = (TioWebsocketRequest) onBeforeBytes.getMethod().invoke(onBeforeBytes.getInstance(), channelContext, bytes);
                onMapEvent(invoke, channelContext);
            }
        }
        return null;
    }

    /**
     * receive text
     * @param wsRequest wsRequest
     * @param text String
     * @param channelContext channelContext
     * @return AnyObject
     * @throws Exception e
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
        log.debug("onText");
        TioWebsocketMethodMapper onText = methods.getOnText();
        if (onText != null) {
            onText.getMethod().invoke(onText.getInstance(), channelContext, text);
        } else {
            TioWebsocketMethodMapper onBeforeText = methods.getOnBeforeText();
            if (onBeforeText != null) {
                TioWebsocketRequest invoke = (TioWebsocketRequest) onBeforeText.getMethod().invoke(onBeforeText.getInstance(), channelContext, text);
                onMapEvent(invoke, channelContext);
            }
        }
        return null;
    }

    private void onMapEvent(TioWebsocketRequest request, ChannelContext channelContext) throws Exception {
        if (request != null && !Strings.isEmpty(request.getEvent())) {
            TioWebsocketMethodMapper methodMapper = methods.getOnMapEvent().get(request.getEvent());
            if (methodMapper != null) {
                methodMapper.getMethod().invoke(methodMapper.getInstance(), channelContext, request.getObject());
            } else {
                TioWebsocketMethodMapper onMap = methods.getOnMap();
                if (onMap != null) {
                    onMap.getMethod().invoke(onMap.getInstance(), channelContext, request.getEvent(), request.getObject());
                }
            }
        }
    }

	public void setMethods(TioWebSocketMethods methods) {
		this.methods = methods;
	}
}
