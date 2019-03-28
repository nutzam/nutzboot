package org.nutz.boot.starter.fescar.aop.lock;

import java.util.concurrent.Callable;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

import com.alibaba.fescar.rm.GlobalLockTemplate;

/**
 * 全局锁
 * @author wendal
 *
 */
public class FescarLockInterceptor implements MethodInterceptor {

    private GlobalLockTemplate<Object> globalLockTemplate = new GlobalLockTemplate<>();

    public void filter(InterceptorChain chain) throws Throwable {
        globalLockTemplate.execute(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    return chain.doChain().getReturn();
                }
                catch (Throwable e) {
                    if (e instanceof Exception) {
                        throw (Exception) e;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

}
