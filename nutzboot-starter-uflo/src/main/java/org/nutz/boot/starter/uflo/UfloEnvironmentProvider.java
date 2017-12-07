package org.nutz.boot.starter.uflo;

import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.bstek.uflo.env.EnvironmentProvider;

public class UfloEnvironmentProvider implements EnvironmentProvider {

    protected SessionFactory sessionFactory;
    protected PlatformTransactionManager platformTransactionManager;

    public String getCategoryId() {
        return null;
    }

    public String getLoginUser() {
        return "anonymous";
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