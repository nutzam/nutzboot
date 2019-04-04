package org.nutz.boot.starter.sentinel.annotation;

import java.lang.reflect.Method;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;

public class SentinelMethodInterceptor implements MethodInterceptor {

    protected String resourceName;
    protected Method method;
    protected Method fallback;
    protected Method blockHandler;
    protected EntryType entryType;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SentinelMethodInterceptor(SentinelResource t, Method method, Ioc ioc) {
        resourceName = t.value();
        if (Strings.isBlank(resourceName)) {
            throw new RuntimeException("@SentinelResource must have value!!! " + method);
        }
        if (!Strings.isBlank(t.fallback())) {
            try {
                fallback = method.getDeclaringClass().getDeclaredMethod(t.fallback(), method.getParameterTypes());
                fallback.setAccessible(true);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("illegal @SentinelResource fallback value at " + method, e);
            }
        }
        if (!Strings.isBlank(t.blockHandler())) {
            try {
                Class[] paramTypes = new Class[method.getParameterCount() + 1];
                System.arraycopy(method.getParameterTypes(), 0, paramTypes, 0, paramTypes.length - 1);
                paramTypes[paramTypes.length - 1] = BlockException.class;
                Class blockHandlerClass = method.getDeclaringClass();
                if (t.blockHandlerClass().length > 0)
                    blockHandlerClass = t.blockHandlerClass()[0];
                blockHandler = blockHandlerClass.getDeclaredMethod(t.blockHandler(), paramTypes);
                blockHandler.setAccessible(true);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("illegal @SentinelResource blockHandler/blockHandlerClass value at " + method, e);
            }
        }
        entryType = t.entryType();
        this.method = method;
    }

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName, entryType, 1, chain.getArgs());
            chain.doChain();
        }
        catch (BlockException ex) {
            if (ex instanceof DegradeException && fallback != null) {
                chain.setReturnValue(fallback.invoke(chain.getCallingObj(), chain.getArgs()));
                return;
            }
            if (blockHandler != null) {
                Object re = blockHandler.invoke(chain.getCallingObj(), chain.getArgs());
                chain.setReturnValue(re);
                return;
            }
            // 啥都不干?
        }
        catch (Throwable ex) {
            Tracer.trace(ex);
            throw ex;
        }
        finally {
            // make sure that the exit() logic is called
            if (entry != null) {
                entry.exit();
            }
        }
    }

}