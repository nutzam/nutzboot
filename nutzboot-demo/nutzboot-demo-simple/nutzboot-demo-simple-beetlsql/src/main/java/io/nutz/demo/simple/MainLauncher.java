package io.nutz.demo.simple;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.beetl.sql.core.OnConnection;
import org.beetl.sql.core.SQLManager;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.demo.simple.bean.User;

@IocBean(create = "init")
public class MainLauncher {

	@Inject
	protected SQLManager beetlsqlManager;

	@At({ "/", "/index" })
	@Ok("beetl:/index.html")
	public User index() {
		User user = new User();
		user.setName("admin");
		return beetlsqlManager.template(user).get(0);
	}

	public void init() {
		// 貌似BeetlSQL没有POJO --> Table的操作?
		beetlsqlManager.executeOnConnection(new OnConnection<Object>() {
			public Object call(Connection conn) throws SQLException {
				Statement st = conn.createStatement();
				st.execute("CREATE TABLE `user` (\r\n" + "      `id` int(11) NOT NULL AUTO_INCREMENT,\r\n"
						+ "      `name` varchar(64) DEFAULT NULL,\r\n" + "      `age` int(4) DEFAULT NULL,"
						+ "PRIMARY KEY (`id`)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
				st.close();
				return null;
			}
		});
		beetlsqlManager.insert(new User("admin", 18));
		beetlsqlManager.insert(new User("root", 21));
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setArgs(args).setPrintProcDoc(true).run();
	}

}
