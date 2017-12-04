package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mongo.ZMo;
import org.nutz.mongo.ZMoCo;
import org.nutz.mongo.ZMoDB;

import io.nutz.demo.simple.bean.User;

@IocBean(create = "init")
public class MainLauncher {

	@Inject
	protected ZMoDB zmodb;

	public void init() {
		ZMoCo co = zmodb.cc("user", true);
		ZMo mo = ZMo.me();
		co.insert(mo.toDoc(new User("apple", 40, "北京")));

		co.insert(mo.toDoc(new User("apple", 40, "北京")));
		co.insert(mo.toDoc(new User("ball", 30, "未知")));
		co.insert(mo.toDoc(new User("cat", 50, "温哥华")));
		co.insert(mo.toDoc(new User("fox", 51, "纽约")));
		co.insert(mo.toDoc(new User("bra", 25, "济南")));
		co.insert(mo.toDoc(new User("lina", 50, "深圳")));
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
