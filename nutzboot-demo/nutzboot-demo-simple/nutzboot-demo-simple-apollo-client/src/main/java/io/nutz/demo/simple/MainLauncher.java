package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

/**
 * 注意, 本demo需要配合 apollo 配置中心的服务端使用
 * @author wendal
 *
 */
@IocBean
public class MainLauncher {
/*
本demo的一些说明:
1. 请先查阅apollo官网的文档,把服务端跑起来,并为SimpleApp项目添加配置 server.port=9080
2. 本项目依赖的是apollo-client-pure,里面没有带apollo-env.properties,主要是演示用途,当然,你需要的使用的话我也不会介意...
3. 生产项目应安装apollo的规范,自行编译apollo-core/apollo-client到私库,并依赖apollo-client
4. main方法中的`System.setProperty("env", "dev")`是为了演示方便,生产环境不应该这样用的.
5. 实现apollo的ConfigChangeListener可实现配置更新的通知,本demo尚未展示.
 */
    @Inject
    protected PropertiesProxy conf;

	@Ok("json:full")
	@At("/config/getall")
	public PropertiesProxy getAll() {
		return conf;
	}

	public static void main(String[] args) throws Exception {
	    System.setProperty("env", "dev");
		new NbApp().setPrintProcDoc(true).run();
	}

}
