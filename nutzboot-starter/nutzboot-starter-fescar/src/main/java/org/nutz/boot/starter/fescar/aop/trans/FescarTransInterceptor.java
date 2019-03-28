/*
 *  Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.nutz.boot.starter.fescar.aop.trans;

import java.lang.reflect.Method;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;

import com.alibaba.fescar.common.exception.ShouldNeverHappenException;
import com.alibaba.fescar.common.util.StringUtils;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.alibaba.fescar.tm.api.DefaultFailureHandlerImpl;
import com.alibaba.fescar.tm.api.FailureHandler;
import com.alibaba.fescar.tm.api.TransactionalExecutor;
import com.alibaba.fescar.tm.api.TransactionalTemplate;

/**
 * The type Global transactional interceptor. 全局事务拦截器
 */
public class FescarTransInterceptor implements MethodInterceptor {

    private static final FailureHandler DEFAULT_FAIL_HANDLER = new DefaultFailureHandlerImpl();

    private final TransactionalTemplate transactionalTemplate = new TransactionalTemplate();
    private final FailureHandler failureHandler;
    private GlobalTransactional globalTrxAnno;
    private String name;

    /**
     * Instantiates a new Global transactional interceptor.
     *
     * @param failureHandler the failure handler
     */
    public FescarTransInterceptor(FailureHandler failureHandler, GlobalTransactional globalTrxAnno, Method method) {
        if (null == failureHandler) {
            failureHandler = DEFAULT_FAIL_HANDLER;
        }
        this.failureHandler = failureHandler;
        this.globalTrxAnno = globalTrxAnno;
        String name = globalTrxAnno.name();
        if (!StringUtils.isNullOrEmpty(name)) {
            this.name = name;
        }
        else 
            this.name = Lang.simpleMethodDesc(method);
    }
    
    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        try {
            transactionalTemplate.execute(new TransactionalExecutor() {
                @Override
                public Object execute() throws Throwable {
                    return chain.doChain();
                }

                @Override
                public int timeout() {
                    return globalTrxAnno.timeoutMills();
                }

                @Override
                public String name() {
                    return name;
                }
            });
        } catch (TransactionalExecutor.ExecutionException e) {
            TransactionalExecutor.Code code = e.getCode();
            switch (code) {
                case RollbackDone:
                    throw e.getOriginalException();
                case BeginFailure:
                    failureHandler.onBeginFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case CommitFailure:
                    failureHandler.onCommitFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case RollbackFailure:
                    failureHandler.onRollbackFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                default:
                    throw new ShouldNeverHappenException("Unknown TransactionalExecutor.Code: " + code);

            }
        }
    }

    
}
