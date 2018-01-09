package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.service.RabbitTestService;

@IocBean(create = "init")
public class MainLauncher {
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
	
	public void init() throws Exception {
		ioc.get(RabbitTestService.class).publish("nutzboot", "hi".getBytes());;
	}

	public static void main(String[] args) {
		new NbApp().setPrintProcDoc(true).run();
	}
}
