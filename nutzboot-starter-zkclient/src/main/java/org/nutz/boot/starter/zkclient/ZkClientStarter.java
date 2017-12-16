package org.nutz.boot.starter.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean
public class ZkClientStarter {
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@IocBean( name = "zkClient")
	public ZkClient getZkClient() {
		String zkServers = conf.get("zk.zkServers", "127.0.0.1:2181");
		int sessionTimeout = conf.getInt("zk.sessionTimeout", 30000);
		int connectionTimeout = conf.getInt("zk.connectionTimeout", Integer.MAX_VALUE);
		long operationRetryTimeout = conf.getInt("zk.operationRetryTimeout", -1);
		String zkSerializer = conf.get("zk.zkSerializer");
		ZkSerializer serializer = null;
		if(!Strings.isBlank(zkSerializer)) {
			serializer = ioc.get(ZkSerializer.class, "zkSerializer");
		}
		if(serializer == null) {
			serializer = new SerializableSerializer();
		}
		return new ZkClient(zkServers,sessionTimeout,connectionTimeout,serializer,operationRetryTimeout); 
	}
}
