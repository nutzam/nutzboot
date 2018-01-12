package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @Author 蛋蛋的忧伤
 * @Time 2018年1月12日 11:41:41
 */
@IocBean
public class MainLauncher {

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
