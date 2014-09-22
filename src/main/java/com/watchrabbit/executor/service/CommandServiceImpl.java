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
import com.watchrabbit.executor.wrapper.CommandConfigWrapper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * @author Mariusz
 */
public class CommandServiceImpl implements CommandService {

    private final ThreadPoolManager poolManager = new ThreadPoolManagerImpl();

    private final CircutBreakerService breakerService = new CircutBreakerServiceImpl();

    @Override
    public <V> Future<V> executeAsynchronously(Callable<V> callable, CommandConfigWrapper command) {
        ExecutorService pool = poolManager.getPool();
        Callable<V> wrappedWithBreaker = breakerService.addCircutBreaker(callable, command.getCommandName());
        return new AsynchronousInvoker().invoke(pool, wrappedWithBreaker);
    }

    @Override
    public <V> Future<V> executeSynchronously(Callable<V> callable, CommandConfigWrapper command) {
        ExecutorService pool = poolManager.getPool();
        Callable<V> wrappedWithBreaker = breakerService.addCircutBreaker(callable, command.getCommandName());
        return new SynchronousInvoker().invoke(pool, wrappedWithBreaker);
    }

}
