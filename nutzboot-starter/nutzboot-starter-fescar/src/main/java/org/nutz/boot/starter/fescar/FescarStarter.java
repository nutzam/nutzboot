package org.nutz.boot.starter.fescar;

import javax.sql.DataSource;

import org.nutz.boot.AppContext;
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
        
        initClient();
        
        DataSource ds = appContext.getIoc().get(DataSource.class);
        if (!(ds instanceof DruidDataSource)) {
            log.error("only DruidDataSource is support by fescar!!!");
            throw new RuntimeException("only DruidDataSource is support by fescar!!!");
        }
        DataSourceProxy proxy = new DataSourceProxy((DruidDataSource) ds, "DEFAULT");
        DataSourceManager.get().registerResource(proxy);
        if (appContext.getIoc().has("dao")) {
            log.info("looking for NutDao instance and replace DataSource");
            NutDao dao = appContext.getIoc().get(NutDao.class, "dao");
            dao.setDataSource(ds);
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
