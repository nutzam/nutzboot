package org.nutz.boot.starter.beetlsql;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
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
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.resource.Scans;

@IocBean
public class BeetlSqlStarter {

	protected static final String PRE = "beetlsql.";

	@PropDoc(group = "beetlsql", value = "数据库类型", defaultValue = "mysql", possible = { "mysql", "oracle", "pgsql" })
	public static final String PROP_DBSTYLE = PRE + "dbStyle";

	@PropDoc(group = "beetlsql", value = "SQL目录", defaultValue = "/sqls/")
	public static final String PROP_PATH = PRE + "path";

	@PropDoc(group = "beetlsql", value = "命名转换方式", defaultValue = "default", possible = { "default", "under_lined" })
	public static final String PROP_NAME_CONVERSION = PRE + "nameconv";

	@PropDoc(group = "beetlsql", value = "是否使用DebugInterceptor", defaultValue = "true")
	public static final String PROP_DEBUG = PRE + "debug";

	@Inject("refer:$ioc")
	protected Ioc ioc;

	@Inject
	protected PropertiesProxy conf;

	@IocBean(name = "beetlsqlDBStyle")
	public DBStyle createDBStyle() throws Exception {
		String type = conf.check(PROP_DBSTYLE);
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
		return ConnectionSourceHelper.getSingle(dataSource);
	}

	@IocBean(name = "beetlsqlManager")
	public SQLManager creatSQLManager(@Inject("refer:beetlsqlDBStyle") DBStyle dbStyle,
			@Inject("beetlsqlConnectionSource") ConnectionSource ds) {
		SQLLoader loader = new ClasspathLoader(conf.get(PROP_PATH, "/sqls/"));
		NameConversion nameconv = "default".equals(conf.get(PROP_NAME_CONVERSION, "default"))
				? new DefaultNameConversion()
				: new UnderlinedNameConversion();
		if (conf.getBoolean(PROP_DEBUG, true))
			return new SQLManager(dbStyle, loader, ds, nameconv, new Interceptor[] { new DebugInterceptor() });
		return new SQLManager(dbStyle, loader, ds, nameconv);
	}
}
