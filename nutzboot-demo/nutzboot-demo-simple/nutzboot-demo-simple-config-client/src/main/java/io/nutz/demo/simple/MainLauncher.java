package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

/**
 * 注意, 本demo需要配合配置中心的服务端使用, 为了演示, 使用的是 http://nbconfig.nutz.cn/ <p/>
 * 
 * 服务端可以是nginx/apache等一切静态文件服务器, 也可以是nutz mvc/nutz boot app等一切能提供web服务的进程
 * @author wendal
 *
 */
@IocBean
public class MainLauncher {
/*
本demo的一些说明:
1. META-INF/app.properties 是可选文件, 其中的参数用于拼接URL,规则是

http://${config.hosts}/${config.zone}/${app.id}/application.properties
http://${config.hosts}/${config.zone}/${app.id}/application-dev.properties
http://${config.hosts}/${config.zone}/${app.id}/application-prod.properties

里面的参数也可以通过 java -Dapp.id=XXX -Dconfig.hosts=YYY myjar.jar 进行设置
2. config.hosts可以是多个域名,用逗号分隔
3. 安全机制是可选的,默认是http basic auth, 以 ${config.zone}作为用户名, ${config.password}作为密码
 */
    @Inject
    protected PropertiesProxy conf;

	@Ok("json:full")
	@At("/config/getall")
	public PropertiesProxy getAll() {
		return conf;
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
