package org.nutz.boot.starter.tio.websocketbean;

/**
 * Tio-Websocket Response for beforeOnText and beforeOnBytes
 */
public class TioWebsocketRequest {
	/**
	 * event
	 */
	private String event;

	/**
	 * request
	 */
	private Object object;

	
	public TioWebsocketRequest(String event, Object object) {
		super();
		this.event = event;
		this.object = object;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
