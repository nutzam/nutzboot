package org.nutz.boot.starter.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean
public class ZkClientStarter {
	
	protected static final String PRE = "zookeeper.";
	
	@PropDoc(group = "zookeeper", value = "服务地址", defaultValue = "127.0.0.1:2181")
	public static final String PROP_ZK_SERVERS = PRE + "zkServers";
	
	@PropDoc(group = "zookeeper", value = "会话超时,单位毫秒", defaultValue = "30000" , type = "int")
	public static final String PROP_SESSION_TIMOUT = PRE + "sessionTimeout";
	
	@PropDoc(group = "zookeeper", value = "连接超时,单位毫秒", defaultValue = "30000" , type = "int")
	public static final String PROP_CONNECTION_TIMOUT = PRE + "connectionTimeout";
	
	@PropDoc(group = "zookeeper", value = "操作超时,单位毫秒", defaultValue = "-1" , type = "long")
	public static final String PROP_OPERATION_RETRY_TIMOUT = PRE + "operationRetryTimeout";
	
	@PropDoc(group = "zookeeper", value = "zookeeper的序列化类", defaultValue = "org.I0Itec.zkclient.serialize.SerializableSerializer" , type = "Object")
	public static final String PROP_SERIALIZER = PRE + "zkSerializer";
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@IocBean( name = "zkClient")
	public ZkClient getZkClient() {	
		return new ZkClient(getZkServers(),getSessionTimeout(),getConnectionTimeout(),getZkSerializer(),getOperationRetryTimeout()); 
	}
	
	public String getZkServers() {
		return conf.get(PROP_ZK_SERVERS, "127.0.0.1:2181");
	}
	
	public int getSessionTimeout() {
		return conf.getInt(PROP_SESSION_TIMOUT, 30000);
	}
	
	public int getConnectionTimeout() {
		return conf.getInt(PROP_CONNECTION_TIMOUT, 30000);
	}
	
	public long getOperationRetryTimeout(){
		return conf.getLong(PROP_OPERATION_RETRY_TIMOUT, -1);
	}
	
	public ZkSerializer getZkSerializer(){
		ZkSerializer serializer = null;
		String zkSerializer = conf.get(PROP_SERIALIZER);
		if(!Strings.isBlank(zkSerializer)) {
			serializer = ioc.get(ZkSerializer.class, zkSerializer);
		}
		if(serializer == null) {
			serializer = new SerializableSerializer();
		}
		return serializer;
	}
}
