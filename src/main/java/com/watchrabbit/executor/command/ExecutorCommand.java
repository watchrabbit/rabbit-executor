/*
 * Copyright 2014 Mariusz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.watchrabbit.executor.command;

import com.watchrabbit.commons.callback.CheckedConsumer;
import com.watchrabbit.commons.exception.SystemException;
import com.watchrabbit.executor.invoker.AsynchronousInvoker;
import com.watchrabbit.executor.invoker.SynchronousInvoker;
import com.watchrabbit.executor.pool.ThreadPoolManager;
import com.watchrabbit.executor.pool.ThreadPoolManagerImpl;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class ExecutorCommand<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorCommand.class);

    private ThreadPoolManager poolManager = new ThreadPoolManagerImpl();

    private String poolName = "default";

    private ExecutorCommand() {

    }

    public V invoke(Callable<V> callable) throws ExecutionException {
        try {
            return new SynchronousInvoker().invoke(poolManager.getPool(poolName), callable).get();
        } catch (InterruptedException ex) {
            LOGGER.error("Shoudl never be thrown!", ex);
            throw new SystemException("Shoudl never be thrown!", ex);
        }
    }

    public Future<V> queue(Callable<V> callable) {
        return new AsynchronousInvoker().invoke(poolManager.getPool(poolName), callable);
    }

    public void observe(Callable<V> callable, CheckedConsumer<V> onSuccess) {
        new AsynchronousInvoker().invoke(poolManager.getPool(poolName), wrap(callable, onSuccess));
    }

    public void observe(Callable<V> callable, CheckedConsumer<V> onSuccess, CheckedConsumer<Exception> onFailure) {
        new AsynchronousInvoker().invoke(poolManager.getPool(poolName), wrap(callable, onSuccess, onFailure));
    }

    private <V> Callable<V> wrap(Callable<V> callable, CheckedConsumer<V> successConsumer) {
        return wrap(callable, successConsumer, (ex) -> LOGGER.debug("Exception thrown by observed callable", ex));
    }

    private <V> Callable<V> wrap(Callable<V> callable, CheckedConsumer<V> successConsumer, CheckedConsumer<Exception> errorConsumer) {
        return () -> {
            try {
                V result = callable.call();
                try {
                    successConsumer.accept(result);
                } catch (Exception ex) {
                    LOGGER.debug("Suppress exception throwed by success callback", ex);
                }
            } catch (Exception ex) {
                errorConsumer.accept(ex);
            }
            return null;
        };
    }

    public static <V> ExecutorCommand<V> executor() {
        return new ExecutorCommand<>();
    }

}
