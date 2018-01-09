package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 这个demo需要配合xxl-job主控端一起使用
 * @author wendal
 *
 */
@IocBean
public class MainLauncher {

	public static void main(String[] args) {
		new NbApp().setPrintProcDoc(true).run();
	}
}
