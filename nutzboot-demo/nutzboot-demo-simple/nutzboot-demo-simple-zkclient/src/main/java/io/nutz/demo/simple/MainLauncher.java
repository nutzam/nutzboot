package io.nutz.demo.simple;

import org.I0Itec.zkclient.ZkClient;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(create = "init")
public class MainLauncher {
	
	@Inject
	private ZkClient zkClient;
	
	public void init() {
		if( !zkClient.exists("/nutzboot") ) {
			zkClient.createEphemeral("/nutzboot");
		}
	}

	public static void main(String[] args) {
		new NbApp().setPrintProcDoc(true).run();
	}
}
