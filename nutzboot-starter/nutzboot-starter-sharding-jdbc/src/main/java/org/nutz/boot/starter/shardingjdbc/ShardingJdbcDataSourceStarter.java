package org.nutz.boot.starter.shardingjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.sql.DataSource;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;

@IocBean
public class ShardingJdbcDataSourceStarter {

	protected static String PRE = "shardingjdbc.";
	@PropDoc(group = "shardingjdbc", value = "配置文件路径", defaultValue = "shardingjdbc.yaml")
	public static final String PROP_PATH = PRE + "path";

	@Inject
	protected PropertiesProxy conf;

	@Inject
	protected AppContext appContext;

	@IocBean
	public DataSource getDataSource() throws Exception {
		String path = conf.get(PROP_PATH, "shardingjdbc.yaml");
		InputStream ins = appContext.getResourceLoader().get(path);
		if (ins == null) {
			File f = new File(path);
			if (f.exists() && f.canRead()) {
				ins = new FileInputStream(f);
			} else {
				throw new RuntimeException("no such shardingjdbc configure file=" + path);
			}
		}
		return ShardingDataSourceFactory.createDataSource(Streams.readBytesAndClose(ins));
	}
}
