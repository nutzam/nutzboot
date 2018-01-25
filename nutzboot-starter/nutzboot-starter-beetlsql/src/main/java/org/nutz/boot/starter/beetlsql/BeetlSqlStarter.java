package org.nutz.boot.starter.beetlsql;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.jdbc.DataSourceStarter;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.resource.Scans;

/**
 * 封装BeetlSql的初始化逻辑
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
@IocBean
public class BeetlSqlStarter {

	protected static final String PRE = "beetlsql.";

	@PropDoc(group = "beetlsql", value = "数据库类型", defaultValue = "mysql", possible = { "mysql", "oracle", "h2", "db2",
			"postgres", "sqlite", "sqlserver", "sqlserver2012" })
	public static final String PROP_DBSTYLE = PRE + "dbStyle";

	@PropDoc(group = "beetlsql", value = "SQL目录", defaultValue = "/sqls/")
	public static final String PROP_PATH = PRE + "path";

	@PropDoc(group = "beetlsql", value = "命名转换方式", defaultValue = "default", possible = { "default", "under_lined" })
	public static final String PROP_NAME_CONVERSION = PRE + "nameconv";

	@PropDoc(group = "beetlsql", value = "是否使用DebugInterceptor", defaultValue = "true")
	public static final String PROP_DEBUG = PRE + "debug";

	@PropDoc(group = "beetlsql", value = "是否启用Trans支持", defaultValue = "true")
	public static final String PROP_TRANS = PRE + "trans";

	@Inject("refer:$ioc")
	protected Ioc ioc;

	@Inject
	protected PropertiesProxy conf;

	@IocBean(name = "beetlsqlDBStyle")
	public DBStyle createDBStyle() throws Exception {
		String type = conf.check(PROP_DBSTYLE);
		// DBStyle的实现类总是 XXXStyle风格的命名,但大小写不一,所以需要循环判断一下
		for (Class<?> klass : Scans.me().scanPackage(AbstractDBStyle.class.getPackage().getName())) {
			if (klass.getName().endsWith("Style")) {
				String name = klass.getSimpleName();
				name = name.substring(0, name.length() - 5);
				if (name.toLowerCase().equals(type))
					return (DBStyle) klass.newInstance();
			}
		}
		throw new RuntimeException("unsupport DBStyle=" + type);
	}

	@IocBean(name = "beetlsqlConnectionSource")
	public ConnectionSource createConnectionSource(@Inject DataSource dataSource) {
	    DataSource[] slaves = null;
        if (Lang.loadClassQuite("org.nutz.boot.starter.jdbc.DataSourceStarter") != null) {
            DataSource slaveDataSource = DataSourceStarter.getSlaveDataSource(ioc, conf);
            if (slaveDataSource != null)
                slaves = new DataSource[] {slaveDataSource};
        }
		if (conf.getBoolean(PROP_TRANS, true)) {
			// 默认事务管理,就是没有管理
			return new DefaultConnectionSource(dataSource, slaves);
		}
		// 支持 Trans.exec 或者 @Aop(TransAop.READ_COMMITTED)
		return new NutzConnectionSource(dataSource, slaves);
	}

	@IocBean(name = "beetlsqlManager")
	public SQLManager creatSQLManager(@Inject("refer:beetlsqlDBStyle") DBStyle dbStyle,
			@Inject("beetlsqlConnectionSource") ConnectionSource ds) {
		// BeetlSql默认/sql/,但NutzBoot的约定是/sqls/,入乡随俗吧
		SQLLoader loader = new ClasspathLoader(conf.get(PROP_PATH, "/sqls/"));
		// TODO 支持更多种类的NameConversion
		NameConversion nameconv = "default".equals(conf.get(PROP_NAME_CONVERSION, "default"))
				? new DefaultNameConversion()
				: new UnderlinedNameConversion();
		// 是否插入debug拦截器呢? 默认启用好了
		if (conf.getBoolean(PROP_DEBUG, true))
			return new SQLManager(dbStyle, loader, ds, nameconv, new Interceptor[] { new DebugInterceptor() });
		return new SQLManager(dbStyle, loader, ds, nameconv);
	}
}
