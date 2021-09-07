package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.integration.zbus.ZbusFactory;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import io.nutz.demo.simple.mq.YvrService;
import io.nutz.demo.simple.mq.YvrSuper;

@IocBean(create="init")
public class MainLauncher {

    @Inject
    ZbusFactory zbus;
    @Inject
    YvrService yvrService;
    @Inject
    YvrSuper yvrSuper;
    
    public void init() throws Exception {
        zbus.init();
        new Thread() {
            public void run() {
                Lang.quiteSleep(3000);
                yvrSuper.getTopicUpdateMq().sync(new YvrMessage());
                Lang.quiteSleep(3000);
                yvrSuper.getTopicUpdateMq().async(new YvrMessage());
            };
        }.start();
    }
    
	public static void main(String[] args) {
		new NbApp().setPrintProcDoc(true).run();
	}
}
