package org.nutz.boot.starter.uflo;

import org.hibernate.SessionFactory;
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.springframework.transaction.PlatformTransactionManager;

import com.bstek.uflo.env.EnvironmentProvider;

public class UfloEnvironmentProvider implements EnvironmentProvider {

    protected SessionFactory sessionFactory;
    protected PlatformTransactionManager platformTransactionManager;
    protected Ioc ioc;
    protected EnvironmentProvider origin;

    public String getCategoryId() {
        checkOrigin();
        if (origin != null)
            return origin.getCategoryId();
        return null;
    }

    public String getLoginUser() {
        checkOrigin();
        if (origin != null)
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