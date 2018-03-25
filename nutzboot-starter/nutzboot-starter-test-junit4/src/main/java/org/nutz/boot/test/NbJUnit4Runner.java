package org.nutz.boot.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.nutz.boot.AppContext;
import org.nutz.boot.NbApp;
import org.nutz.conf.NutConf;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

public class NbJUnit4Runner extends BlockJUnit4ClassRunner {

    protected ThreadLocal<NbApp> appHolder = new ThreadLocal<>();

    protected boolean hasIocBean;
    protected Method createNbAppMethod;
    protected List<Field> fields = new LinkedList<>();

    public NbJUnit4Runner(Class<?> klass) throws InitializationError {
        super(klass);
        hasIocBean = klass.getAnnotation(IocBean.class) != null;
        try {
            createNbAppMethod = klass.getMethod("createNbApp");
            createNbAppMethod.setAccessible(true);
        }
        catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        if (!hasIocBean) {
            for (Field field : klass.getDeclaredFields()) {
                if (field.getAnnotation(Inject.class) != null) {
                    fields.add(field);
                    if (!field.isAccessible())
                        field.setAccessible(true);
                }
            }
        }
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (isIgnored(method)) {
            super.runChild(method, notifier);
            return;
        }
        NutConf.AOP_USE_CLASS_ID = true;
        NbApp app = createNbApp();
        appHolder.set(app);
        try {
            app.execute();
            super.runChild(method, notifier);
        }
        finally {
            appHolder.remove();
            app.shutdown();
            AppContext.setDefault(new AppContext());
        }
    }

    /**
     * 子类应该覆盖这个方法,返回一个自定义mainClass的NbApp实例
     */
    protected NbApp createNbApp() {
        if (createNbAppMethod == null)
            return new NbApp(getTestClass().getJavaClass());
        try {
            return (NbApp) createNbAppMethod.invoke(null);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object createTest() throws Exception {
        if (hasIocBean)
            return getIoc().get(getTestClass().getJavaClass());
        Object obj = getTestClass().getJavaClass().newInstance();
        for (Field field : fields) {
            field.set(obj, getIoc().getByType(field.getType()));
        }
        return obj;
    }
    
    protected Ioc getIoc() {
        return appHolder.get().getAppContext().getIoc();
    }

}
