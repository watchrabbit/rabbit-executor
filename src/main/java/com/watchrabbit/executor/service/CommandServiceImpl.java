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
package com.watchrabbit.executor.service;

import com.watchrabbit.executor.invoker.AsynchronousInvoker;
import com.watchrabbit.executor.invoker.SynchronousInvoker;
import com.watchrabbit.executor.pool.ThreadPoolManager;
import com.watchrabbit.executor.pool.ThreadPoolManagerImpl;
import com.watchrabbit.executor.wrapper.CommandConfig;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * @author Mariusz
 */
public class CommandServiceImpl implements CommandService {

    private final ThreadPoolManager poolManager = new ThreadPoolManagerImpl();

    private final CircuitBreakerService breakerService = new CircuitBreakerServiceImpl();

    private final CacheService cacheService = new CacheServiceImpl();

    private final RetryService retryService = new RetryServiceImpl();

    @Override
    public <V> Future<V> executeAsynchronously(Callable<V> callable, CommandConfig command) {
        ExecutorService pool = poolManager.getPool();
        Callable<V> wrapped = wrapp(callable, pool, command);
        return new AsynchronousInvoker().invoke(pool, wrapped);
    }

    @Override
    public <V> V executeSynchronously(Callable<V> callable, CommandConfig command) throws Exception {
        ExecutorService pool = poolManager.getPool();
        Callable<V> wrapped = wrapp(callable, pool, command);
        return new SynchronousInvoker().invoke(pool, wrapped);
    }

    private <V> Callable<V> wrapp(Callable<V> callable, ExecutorService pool, CommandConfig command) {
        Callable<V> wrapped = breakerService.addCircuitBreaker(callable, command);
        if (command.getCacheConfig() != null) {
            wrapped = cacheService.addCache(wrapped, command.getCacheConfig());
        }
        if (command.getRetryConfig() != null) {
            wrapped = retryService.addRetry(wrapped, pool, command.getRetryConfig());
        }
        return wrapped;
    }
}
