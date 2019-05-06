package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class MainLauncher {

	public static void main(String[] args) {
		new NbApp().setPrintProcDoc(true).run();
	}
}
