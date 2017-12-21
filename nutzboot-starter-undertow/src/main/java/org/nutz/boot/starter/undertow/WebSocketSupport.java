package org.nutz.boot.starter.undertow;

import javax.websocket.server.ServerEndpoint;

import org.nutz.resource.Scans;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * websocket支持.
 * 如果项目中不需要使用websocket，则可以在自己项目的pom.xml中排除掉undertow-websockets-jsr即可
 * 
 * @author qinerg(qinerg@gmail.com)
 */
public class WebSocketSupport {

	public static void addWebSocketSupport(DeploymentInfo deployment, String packageName) {
		WebSocketDeploymentInfo wsInfo = new WebSocketDeploymentInfo();
		for (Class<?> klass : Scans.me().scanPackage(packageName)) {
			if (klass.getAnnotation(ServerEndpoint.class) != null) {
				wsInfo.addEndpoint(klass);
			}
		}

		deployment.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, wsInfo);
	}
}
