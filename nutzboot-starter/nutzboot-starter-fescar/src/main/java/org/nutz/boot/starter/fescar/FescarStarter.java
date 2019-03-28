package org.nutz.boot.starter.fescar;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fescar.common.util.StringUtils;
import com.alibaba.fescar.config.ConfigurationFactory;
import com.alibaba.fescar.rm.RMClient;
import com.alibaba.fescar.rm.datasource.DataSourceManager;
import com.alibaba.fescar.rm.datasource.DataSourceProxy;
import com.alibaba.fescar.tm.TMClient;

@IocBean
public class FescarStarter implements ServerFace {
    
    private static final Log log = Logs.get();

    protected static final String PRE = "fescar.";
    
    @PropDoc(value="fescar应用id", need=true)
    public static String PROP_APPID = PRE + "applicationId";
    
    @PropDoc(value="fescar事务组", need=true)
    public static String PROP_TXGROUP = PRE + "txServiceGroup";
    
    @PropDoc(value="自动创建undo表", defaultValue="true")
    public static String PROP_CREATE_UNDO = PRE + "create_undo_table";
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    protected AppContext appContext;
    
    private final boolean disableGlobalTransaction = ConfigurationFactory.getInstance().getBoolean("service.disableGlobalTransaction", false);
    
    private String applicationId;
    private String txServiceGroup;
    
    @Override
    public void start() throws Exception {
        if (disableGlobalTransaction) {
            if (log.isInfoEnabled()) {
                log.info("Global transaction is disabled.");
            }
            return;
        }
        if (!conf.getBoolean("fescar.enable", true)) {
            log.info("Global transaction is disabled.");
            return;
        }
        applicationId = conf.check(PROP_APPID);
        txServiceGroup = conf.check(PROP_TXGROUP);
        log.infof("fescar applicationId=%s txServiceGroup=%s", applicationId, txServiceGroup);
        
        initClient();
        
        DataSource ds = appContext.getIoc().get(DataSource.class);
        if (!(ds instanceof DruidDataSource)) {
            log.error("only DruidDataSource is support by fescar!!!");
            throw new RuntimeException("only DruidDataSource is support by fescar!!!");
        }
        if (conf.getBoolean(PROP_CREATE_UNDO, true)) {
            try (Connection conn = ds.getConnection()) {
                Statement st = conn.createStatement();
                st.execute("CREATE TABLE IF NOT EXISTS " + "`undo_log` (\r\n" + 
                        "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\r\n" + 
                        "  `branch_id` bigint(20) NOT NULL,\r\n" + 
                        "  `xid` varchar(100) NOT NULL,\r\n" + 
                        "  `rollback_info` longblob NOT NULL,\r\n" + 
                        "  `log_status` int(11) NOT NULL,\r\n" + 
                        "  `log_created` datetime NOT NULL,\r\n" + 
                        "  `log_modified` datetime NOT NULL,\r\n" + 
                        "  `ext` varchar(100) DEFAULT NULL,\r\n" + 
                        "  PRIMARY KEY (`id`),\r\n" + 
                        "  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)\r\n" + 
                        ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8");
                st.close();
            }
        }
        DataSourceProxy proxy = new DataSourceProxy((DruidDataSource) ds, "DEFAULT");
        DataSourceManager.get().registerResource(proxy);
        if (appContext.getIoc().has("dao")) {
            log.info("looking for NutDao instance and replace DataSource");
            NutDao dao = appContext.getIoc().get(NutDao.class, "dao");
            dao.setDataSource(proxy);
        }
        else {
            log.info("NutDao instance not found, skip it");
        }
    }
    
    protected void initClient() {
        if (log.isInfoEnabled()) {
            log.info("Initializing Global Transaction Clients ... ");
        }
        if (StringUtils.isNullOrEmpty(applicationId) || StringUtils.isNullOrEmpty(txServiceGroup)) {
            throw new IllegalArgumentException(
                "applicationId: " + applicationId + ", txServiceGroup: " + txServiceGroup);
        }
        //init TM
        TMClient.init(applicationId, txServiceGroup);
        if (log.isInfoEnabled()) {
            log.info(
                "Transaction Manager Client is initialized. applicationId[" + applicationId + "] txServiceGroup["
                    + txServiceGroup + "]");
        }
        //init RM
        RMClient.init(applicationId, txServiceGroup);
        if (log.isInfoEnabled()) {
            log.info("Resource Manager is initialized. applicationId[" + applicationId  + "] txServiceGroup["  + txServiceGroup + "]");
        }

        if (log.isInfoEnabled()) {
            log.info("Global Transaction Clients are initialized. ");
        }
    }
}
