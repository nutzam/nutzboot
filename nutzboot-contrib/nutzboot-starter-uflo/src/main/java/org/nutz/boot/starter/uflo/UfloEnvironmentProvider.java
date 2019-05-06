package org.nutz.boot.starter.uflo;

import org.hibernate.SessionFactory;
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.transaction.PlatformTransactionManager;

import com.bstek.uflo.env.EnvironmentProvider;

public class UfloEnvironmentProvider implements EnvironmentProvider {
    
    private static final Log log = Logs.get();

    protected SessionFactory sessionFactory;
    protected PlatformTransactionManager platformTransactionManager;
    protected Ioc ioc;
    protected EnvironmentProvider origin;

    public String getCategoryId() {
        checkOrigin();
        if (origin != null && origin != this)
            return origin.getCategoryId();
        return null;
    }

    public String getLoginUser() {
        checkOrigin();
        if (origin != null && origin != this)
            return origin.getLoginUser();
        return "anonymous";
    }
    
    protected void checkOrigin() {
        if (ioc == null) {
            ioc = AppContext.getDefault().getIoc();
            if (ioc.has("uflo.environmentProvider")) {
                origin = ioc.get(EnvironmentProvider.class, "uflo.environmentProvider");
            }
        }
        if (origin == this) {
            log.warn("you have to define a class implements EnvironmentProvider and mask @IocBean(name='uflo.environmentProvider')");
        }
    }

    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}