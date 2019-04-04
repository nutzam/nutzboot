package org.nutz.boot.starter.tio.websocket.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnAfterHandshake;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnBeforeBytes;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnBeforeText;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnBytes;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnClose;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnHandshake;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnMap;
import org.nutz.boot.starter.tio.websocket.annotation.TIOnText;
import org.nutz.boot.starter.tio.websocket.annotation.TioController;
import org.nutz.boot.starter.tio.websocket.exception.TioWebsocketException;
import org.nutz.boot.starter.tio.websocketbean.TioWebSocketMethods;
import org.nutz.boot.starter.tio.websocketbean.TioWebsocketMethodMapper;
import org.nutz.boot.starter.tio.websocketbean.TioWebsocketMsgHandler;
import org.nutz.boot.starter.tio.websocketbean.TioWebsocketRequest;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.server.WsServerConfig;
import org.tio.websocket.server.WsServerStarter;

/**
 *
 * @Author 科技
 * @Time 2019年02月20日 19:00:01
 */
@IocBean(create = "init")
public class TioWebsocketStarter implements ServerFace {

	private final static Log log = Logs.get();

	protected static final String PRE = "tio.websocket.";

	@Inject
	protected PropertiesProxy conf;

	@PropDoc(group = "tio.websocket", value = "tio监听端口", defaultValue = "9421")
	public static final String PROP_PORT = PRE + "port";

	@PropDoc(group = "tio.websocket", value = "tio监听的ip", defaultValue = "0.0.0.0")
	public static final String PROP_IP = PRE + "host";

	@PropDoc(group = "tio.websocket", value = "是否启动框架层面心跳", defaultValue = "false")
	public static final String PROP_HEARTBEAT = PRE + "heartbeat";

	@PropDoc(group = "tio.websocket", value = "心跳超时时间(单位:毫秒)", defaultValue = "120000")
	public static final String PROP_HEARTBEATTIMEOUT = PRE + "heartbeatTimeout";

	@PropDoc(group = "tio.websocket", value = "上下文名称", defaultValue = "NutzBoot WebSocket GroupContext")
	public static final String PROP_NAME = PRE + "name";

	private WsServerStarter starter;

	private TioWebSocketMethods methods = new TioWebSocketMethods();

	@Inject
	private AppContext appContext;

	@IocBean(name = "webSocketServerGroupContext")
	public ServerGroupContext getServerGroupContext() throws Exception {
		WsServerConfig wsConfig = new WsServerConfig(appContext.getServerPort(PROP_PORT, 9420));
		wsConfig.setBindIp(appContext.getServerHost(PROP_IP));
		wsConfig.setCharset("utf-8");
		starter = new WsServerStarter(wsConfig, new TioWebsocketMsgHandler(methods));
		ServerGroupContext groupContext = starter.getServerGroupContext();
		groupContext.setName(conf.get(PROP_NAME, "NutzBoot GroupContext"));
		groupContext.setHeartbeatTimeout(0);
		if ("true".equals(conf.get(PROP_HEARTBEAT))) {
			groupContext.setHeartbeatTimeout(conf.getLong(PROP_HEARTBEATTIMEOUT, 120000));
		}
		return groupContext;
	}

	public void start() throws Exception {
		log.debug("init AioWebsocket Server ...");
		this.starter.start();
	}

	public void stop() throws Exception {
		
	}

	public void init() throws IOException {
		Set<Class<?>> classes = new LinkedHashSet<>();
		String[] neanNames = appContext.getIoc().getNamesByAnnotation(TioController.class);
		for (String beanName : neanNames) {
			try {
				classes.add(appContext.getIoc().getType(beanName));
			} catch (ObjectLoadException e) {
				log.error(e);
			}
		}

		classes.forEach(item -> {
			Method[] currentClazzMethods = item.getDeclaredMethods();
			Object bean = appContext.getIoc().get(item);
			for (Method method : currentClazzMethods) {
				if (!Modifier.isPublic(method.getModifiers())) {
					continue;
				}
				if (method.isAnnotationPresent(TIOnHandshake.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(void.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 3 || !parameterTypes[0].equals(HttpRequest.class) || !parameterTypes[1].equals(HttpResponse.class) || !parameterTypes[2].equals(ChannelContext.class)) {
						continue;
					}
					if (methods.getHandshake() != null) {
						throw new TioWebsocketException("duplicate TIOnHandshake");
					}
					methods.setHandshake(new TioWebsocketMethodMapper(bean, method));
				} else if (method.isAnnotationPresent(TIOnAfterHandshake.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(void.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 3 || !parameterTypes[0].equals(HttpRequest.class) || !parameterTypes[1].equals(HttpResponse.class) || !parameterTypes[2].equals(ChannelContext.class)) {
						continue;
					}
					if (methods.getOnAfterHandshaked() != null) {
						throw new TioWebsocketException("duplicate TIOnAfterHandshake");
					}
					methods.setOnAfterHandshaked(new TioWebsocketMethodMapper(bean, method));
				} else if (method.isAnnotationPresent(TIOnClose.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(void.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 1 || !parameterTypes[0].equals(ChannelContext.class)) {
						continue;
					}
					if (methods.getOnClose() != null) {
						throw new TioWebsocketException("duplicate TIOnClose");
					}
					methods.setOnClose(new TioWebsocketMethodMapper(bean, method));
				} else if (method.isAnnotationPresent(TIOnBeforeBytes.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(TioWebsocketRequest.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 2 || !parameterTypes[0].equals(ChannelContext.class) || !parameterTypes[1].equals(byte[].class)) {
						continue;
					}
					if (methods.getOnBeforeBytes() != null) {
						throw new TioWebsocketException("duplicate TIOnBeforeBytes");
					}
					methods.setOnBeforeBytes(new TioWebsocketMethodMapper(bean, method));
				} else if (method.isAnnotationPresent(TIOnBeforeText.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(TioWebsocketRequest.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 2 || !parameterTypes[0].equals(ChannelContext.class) || !parameterTypes[1].equals(String.class)) {
						continue;
					}
					if (methods.getOnBeforeText() != null) {
						throw new TioWebsocketException("duplicate TIOnBeforeText");
					}
					methods.setOnBeforeText(new TioWebsocketMethodMapper(bean, method));
				} else if (method.isAnnotationPresent(TIOnText.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(void.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 2 || !parameterTypes[0].equals(ChannelContext.class) || !parameterTypes[1].equals(String.class)) {
						continue;
					}
					if (methods.getOnText() != null) {
						throw new TioWebsocketException("duplicate TIOnText");
					}
					methods.setOnText(new TioWebsocketMethodMapper(bean, method));

				} else if (method.isAnnotationPresent(TIOnBytes.class)) {
					Class<?> returnType = method.getReturnType();
					if (!returnType.equals(void.class)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 2 || !parameterTypes[0].equals(ChannelContext.class) || !parameterTypes[1].equals(byte[].class)) {
						continue;
					}

					if (methods.getOnBytes() != null) {
						throw new TioWebsocketException("duplicate TIOnBytes");
					}

					methods.setOnBytes(new TioWebsocketMethodMapper(bean, method));
				} else if (method.isAnnotationPresent(TIOnMap.class)) {
					TIOnMap annotation = method.getAnnotation(TIOnMap.class);
					if (annotation.value().length() == 0) {
						Class<?> returnType = method.getReturnType();
						if (!returnType.equals(void.class)) {
							continue;
						}
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length != 3 || !parameterTypes[0].equals(ChannelContext.class) || !parameterTypes[1].equals(String.class)) {
							continue;
						}
						if (methods.getOnMap() != null) {
							throw new TioWebsocketException("duplicate default TIOnMap");
						}
						methods.setOnMap(new TioWebsocketMethodMapper(bean, method));
					} else {
						Class<?> returnType = method.getReturnType();
						if (!returnType.equals(void.class)) {
							continue;
						}
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length != 2 || !parameterTypes[0].equals(ChannelContext.class)) {
							continue;
						}
						if (methods.getOnMapEvent().get(annotation.value()) != null) {
							throw new TioWebsocketException("duplicate TIOnMap with event " + annotation.value());
						}
						methods.getOnMapEvent().put(annotation.value(), new TioWebsocketMethodMapper(bean, method));
					}
				}
			}
		});

		if (methods.getHandshake() == null) {
			throw new TioWebsocketException("miss @TIOnHandshake");
		}
		if (methods.getOnClose() == null) {
			throw new TioWebsocketException("miss @TIOnClose");
		}

		TioWebsocketMsgHandler handler = (TioWebsocketMsgHandler) starter.getWsMsgHandler();
		handler.setMethods(methods);
	}
}
