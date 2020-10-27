package org.nutz.boot.test;

import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.nutz.boot.AppContext;
import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.lang.reflect.Field;

public class NutzBootExtension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor,
        BeforeEachCallback, AfterEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback,
        ParameterResolver {
    private final static Log log = Logs.get();
    /**
     * {@link Namespace} in which {@code TestContextManagers} are stored,
     * keyed by test class.
     */
    private static final Namespace NAMESPACE = Namespace.create(NutzBootExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getTestContextManager(context).execute();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        try {
            getTestContextManager(context).shutdown();
        }
        finally {
            getStore(context).remove(context.getRequiredTestClass());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {

    }



    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        _init_fields(getApplicationContext(context).getIoc(),testInstance);
    }

    /**
     * 遍历当前对象中的属性,如果标注了@Inject则从ioc容器取出对象,注入进去
     * @throws Exception 注入过程中如果抛出异常
     */
    public void _init_fields(Ioc ioc,Object testInstance) throws Exception {
        Field[] fields = testInstance.getClass().getDeclaredFields();
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject == null) {
                continue;
            }
            String val = inject.value();
            Object v = null;
            if (Strings.isBlank(val)) {
                v = ioc.get(field.getType(), field.getName());
            } else {
                if (val.startsWith("refer:")) {
                    val = val.substring("refer:".length());
                }
                v = ioc.get(field.getType(), val);
            }
            field.setAccessible(true);
            field.set(testInstance, v);
        }
    }
    /**
     * Get the {@link AppContext} associated with the supplied {@code ExtensionContext}.
     * @param context the current {@code ExtensionContext} (never {@code null})
     * @return the application context
     * @throws IllegalStateException if an error occurs while retrieving the application context
     * //@see org.springframework.test.context.TestContext#getApplicationContext()
     */
    public static AppContext getApplicationContext(ExtensionContext context) {
        return getTestContextManager(context).getAppContext();
    }

    /**
     * Get the {@link NbApp} associated with the supplied {@code ExtensionContext}.
     * @return the {@code NbApp} (never {@code null})
     */
    private static NbApp getTestContextManager(ExtensionContext context) {
        //Assert.notNull(context, "ExtensionContext must not be null");
        Class<?> testClass = context.getRequiredTestClass();
        Store store = getStore(context);
        return store.getOrComputeIfAbsent(testClass, NbApp::new, NbApp.class).setPrintProcDoc(true);
    }


    private static Store getStore(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE);
    }
}
