package org.nutz.boot.starter.hystrix;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Strings;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.GenericSetterBuilder;
import com.netflix.hystrix.contrib.javanica.exception.HystrixCachingException;

/**
 * 
 */
public class HystrixCommandInterceptor implements MethodInterceptor {

    protected HystrixCommand hystrixCommand;
    protected String groupKey;
    protected String commandKey;
    protected FastMethod fallbackMethod;
    protected String threadPoolKey;
    protected HystrixProperty[] commandproperties;
    protected HystrixProperty[] threadPoolProperties;
    protected FastMethod defaultFallback;
    protected Class<? extends Throwable>[] ignoreExceptions;
    protected HystrixException[] raiseHystrixExceptions;
    protected ObservableExecutionMode observableExecutionMode;
    
    
    public HystrixCommandInterceptor(HystrixCommand hystrixCommand, Method method) throws NoSuchMethodException, SecurityException {
        groupKey = Strings.sBlank(hystrixCommand.groupKey(), method.getDeclaringClass().getName());
        commandKey = Strings.sBlank(hystrixCommand.commandKey(), method.getName());
        if (!Strings.isBlank(hystrixCommand.fallbackMethod())) {
            Method fb = method.getDeclaringClass().getMethod(hystrixCommand.fallbackMethod(), Throwable.class);
            fallbackMethod = FastClassFactory.get(fb);
        }
        else if (!Strings.isBlank(hystrixCommand.defaultFallback())) {
            Method fb = method.getDeclaringClass().getMethod(hystrixCommand.defaultFallback());
            defaultFallback = FastClassFactory.get(fb);
        }
        threadPoolKey = hystrixCommand.threadPoolKey();
        commandproperties = hystrixCommand.commandProperties();
        threadPoolProperties = hystrixCommand.threadPoolProperties();
        ignoreExceptions = hystrixCommand.ignoreExceptions();
        raiseHystrixExceptions = hystrixCommand.raiseHystrixExceptions();
        observableExecutionMode = hystrixCommand.observableExecutionMode();
    }

    public void filter(InterceptorChain chain) throws Throwable {
        GenericSetterBuilder setter = GenericSetterBuilder.builder()
                .commandKey(commandKey)
                .groupKey(groupKey)
                .threadPoolKey(threadPoolKey)
                .threadPoolProperties(Arrays.asList(threadPoolProperties))
                .commandProperties(Arrays.asList(commandproperties)).build();
        com.netflix.hystrix.HystrixCommand<Object> cmd = new com.netflix.hystrix.HystrixCommand<Object>(setter.build()) {
            
            protected Object run() throws Exception {
                try {
                    return chain.doChain().getReturn();
                }
                catch (Throwable e) {
                    for (Class<? extends Throwable> klass : ignoreExceptions) {
                        if (klass.isAssignableFrom(e.getClass()))
                            throw new HystrixCachingException(e);
                    }
                    if (e instanceof Exception)
                        throw (Exception)e;
                    throw new Exception(e);
                }
            }

            protected Object getFallback() {
                try {
                    if (fallbackMethod != null)
                        return fallbackMethod.invoke(chain.getCallingObj(), chain.getArgs());
                    else if (defaultFallback != null)
                        return defaultFallback.invoke(chain.getCallingObj()) ;
                }
                catch (Exception e) {
                }
                return null;
            }
        };
        cmd.execute();
    }
}
