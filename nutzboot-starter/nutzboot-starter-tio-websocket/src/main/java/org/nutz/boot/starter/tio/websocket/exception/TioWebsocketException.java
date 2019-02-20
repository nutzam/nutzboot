package org.nutz.boot.starter.tio.websocket.exception;

public class TioWebsocketException extends RuntimeException {

	private static final long serialVersionUID = 7895271036523364055L;

	public TioWebsocketException() {
	}

	public TioWebsocketException(String message) {
		super(message);
	}

	public TioWebsocketException(String message, Throwable cause) {
		super(message, cause);
	}

	public TioWebsocketException(Throwable cause) {
		super(cause);
	}

	public TioWebsocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
