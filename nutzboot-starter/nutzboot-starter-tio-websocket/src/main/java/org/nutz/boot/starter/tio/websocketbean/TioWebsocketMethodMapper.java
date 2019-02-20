package org.nutz.boot.starter.tio.websocketbean;

import java.lang.reflect.Method;

/**
 * Tio-Websocket Clazz-Method Mapper
 */
public class TioWebsocketMethodMapper {
	
    private Object instance;
    private Method method;
    
    
	public TioWebsocketMethodMapper(Object instance, Method method) {
		super();
		this.instance = instance;
		this.method = method;
	}
	public Object getInstance() {
		return instance;
	}
	public void setInstance(Object instance) {
		this.instance = instance;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
    
    
}
