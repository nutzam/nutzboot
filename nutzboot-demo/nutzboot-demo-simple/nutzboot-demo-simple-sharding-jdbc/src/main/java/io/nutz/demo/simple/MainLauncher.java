package io.nutz.demo.simple;

import java.util.List;

import org.nutz.boot.NbApp;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;

import io.nutz.demo.simple.bean.UserOrder;

@IocBean(create = "init")
public class MainLauncher {

	private static final Log log = Logs.get();

	@Inject
	protected Dao dao;

	@At("/user/query/?")
	public List<UserOrder> queryByUserId(int userId) {
		return dao.query(UserOrder.class, Cnd.where("userId", "=", 1));
	}

	public void init() {
		// 建表及插入测试数据
		// shardingjdbc的表需要自己建,为了demo,这里只能迁就一下...
		creare_order_table();

		// 测试一下查询语句
		List<UserOrder> orders = dao.query(UserOrder.class, Cnd.where("userId", "=", 1));
		log.info("User A orders = " + Json.toJson(orders));
		orders = dao.query(UserOrder.class, Cnd.where("userId", "=", 2));
		log.info("User B orders = " + Json.toJson(orders));
	}

	protected void creare_order_table() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				SimpleDataSource ds = new SimpleDataSource();
				ds.setJdbcUrl("jdbc:mysql://localhost:3306/ds_" + i);
				ds.setUsername("root");
				ds.setPassword("root");
				NutDao dao = new NutDao(ds);
				Daos.ext(dao, "_" + j).create(UserOrder.class, false);
			}
		}
		// 分别为userId=1和userId=2插入2条记录
		if (dao.count(UserOrder.class, Cnd.where("userId", "=", 1)) == 0) {
			dao.insert(new UserOrder(1, 11));
			dao.insert(new UserOrder(1, 18));
		}
		if (dao.count(UserOrder.class, Cnd.where("userId", "=", 2)) == 0) {
			dao.insert(new UserOrder(2, 12));
			dao.insert(new UserOrder(2, 9));
		}
	}

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
