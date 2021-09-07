package org.nutz.boot.starter.shardingjdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;

@IocBean
public class ShardingJdbcDataSourceStarter {

	protected static String PRE = "shardingsphere.";
	@PropDoc(group = "shardingsphere", value = "配置文件路径", defaultValue = "shardingsphere.yaml")
	public static final String PROP_PATH = PRE + "path";

	@Inject
	protected PropertiesProxy conf;

	@Inject
	protected AppContext appContext;

	@IocBean
	public DataSource getDataSource() throws Exception {
		String path = conf.get(PROP_PATH, "shardingsphere.yaml");
		InputStream ins = appContext.getResourceLoader().get(path);
		if (ins == null) {
			File f = new File(path);
			if (f.exists() && f.canRead()) {
				ins = new FileInputStream(f);
			} else {
				throw new RuntimeException("no such shardingsphere yaml configure file=" + path);
			}
		}
		return YamlShardingDataSourceFactory.createDataSource(Streams.readBytesAndClose(ins));
	}
}
