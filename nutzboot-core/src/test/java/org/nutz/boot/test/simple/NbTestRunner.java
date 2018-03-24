package org.nutz.boot.test.simple;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.nutz.boot.NbApp;
import org.nutz.conf.NutConf;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;

public class NbTestRunner extends BlockJUnit4ClassRunner {
    
    protected ThreadLocal<Ioc> iocHolder = new ThreadLocal<Ioc>();

    public NbTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        if (klass.getAnnotation(IocBean.class) == null)
            throw new InitializationError("Must mark as @IocBean");
    }
    
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (isIgnored(method)) {
            super.runChild(method, notifier);
            return;
        }
        NutConf.AOP_USE_CLASS_ID = true;
        NbApp app = createNbApp();
        try {
            app.execute();
            iocHolder.set(app.getAppContext().getIoc());
            super.runChild(method, notifier);
        } finally {
            iocHolder.remove();
            app.shutdown();
        }
    }
    /**
     * 子类应该覆盖这个方法,返回一个自定义mainClass的NbApp实例
     */
    protected NbApp createNbApp() {
        return new NbApp(getTestClass().getJavaClass());
    }
    
    protected Object createTest() throws Exception {
        return iocHolder.get().get(getTestClass().getJavaClass());
    }
    
}
