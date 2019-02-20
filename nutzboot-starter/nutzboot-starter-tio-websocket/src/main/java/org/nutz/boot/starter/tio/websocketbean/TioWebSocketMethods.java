package org.nutz.boot.starter.tio.websocketbean;

import java.util.HashMap;

/**
 * Tio-WebSocket methods
 * 
 * @author zhfish
 */
public class TioWebSocketMethods {
	private TioWebsocketMethodMapper handshake;
	private TioWebsocketMethodMapper onAfterHandshaked;
	private TioWebsocketMethodMapper onClose;
	private TioWebsocketMethodMapper onBeforeText;
	private TioWebsocketMethodMapper onBeforeBytes;
	private TioWebsocketMethodMapper onText;
	private TioWebsocketMethodMapper onBytes;
	private TioWebsocketMethodMapper onMap;

	private HashMap<String, TioWebsocketMethodMapper> onMapEvent = new HashMap<>();

	public TioWebsocketMethodMapper getHandshake() {
		return handshake;
	}

	public void setHandshake(TioWebsocketMethodMapper handshake) {
		this.handshake = handshake;
	}

	public TioWebsocketMethodMapper getOnAfterHandshaked() {
		return onAfterHandshaked;
	}

	public void setOnAfterHandshaked(TioWebsocketMethodMapper onAfterHandshaked) {
		this.onAfterHandshaked = onAfterHandshaked;
	}

	public TioWebsocketMethodMapper getOnClose() {
		return onClose;
	}

	public void setOnClose(TioWebsocketMethodMapper onClose) {
		this.onClose = onClose;
	}

	public TioWebsocketMethodMapper getOnBeforeText() {
		return onBeforeText;
	}

	public void setOnBeforeText(TioWebsocketMethodMapper onBeforeText) {
		this.onBeforeText = onBeforeText;
	}

	public TioWebsocketMethodMapper getOnBeforeBytes() {
		return onBeforeBytes;
	}

	public void setOnBeforeBytes(TioWebsocketMethodMapper onBeforeBytes) {
		this.onBeforeBytes = onBeforeBytes;
	}

	public TioWebsocketMethodMapper getOnText() {
		return onText;
	}

	public void setOnText(TioWebsocketMethodMapper onText) {
		this.onText = onText;
	}

	public TioWebsocketMethodMapper getOnBytes() {
		return onBytes;
	}

	public void setOnBytes(TioWebsocketMethodMapper onBytes) {
		this.onBytes = onBytes;
	}

	public TioWebsocketMethodMapper getOnMap() {
		return onMap;
	}

	public void setOnMap(TioWebsocketMethodMapper onMap) {
		this.onMap = onMap;
	}

	public HashMap<String, TioWebsocketMethodMapper> getOnMapEvent() {
		return onMapEvent;
	}

}
