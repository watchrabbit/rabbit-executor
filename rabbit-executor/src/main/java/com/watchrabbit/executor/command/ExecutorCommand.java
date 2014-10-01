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
import com.watchrabbit.executor.service.CommandService;
import com.watchrabbit.executor.service.CommandServiceImpl;
import com.watchrabbit.executor.wrapper.CacheConfig;
import com.watchrabbit.executor.wrapper.CheckedRunnable;
import com.watchrabbit.executor.wrapper.CommandConfig;
import com.watchrabbit.executor.wrapper.RetryConfig;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class ExecutorCommand<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorCommand.class);

    private final CommandService service = new CommandServiceImpl();

    private final CommandConfig config;

    protected ExecutorCommand(CommandConfig config) {
        this.config = config;
    }

    /**
     * Adds cache logic to callable processing.
     *
     * @param cacheConfig cache configuration used by executor
     * @return {@code ExecutorCommand<V>} executor with cache configuration
     */
    public ExecutorCommand<V> withCache(CacheConfig cacheConfig) {
        config.setCacheConfig(cacheConfig);
        return this;
    }

    /**
     * Adds retry logic to callable processing.
     *
     * @param retryConfig retry configuration used by executor
     * @return {@code ExecutorCommand<V>} executor with retry configuration
     */
    public ExecutorCommand<V> withRetry(RetryConfig retryConfig) {
        config.setRetryConfig(retryConfig);
        return this;
    }

    /**
     * After {@code breakerRetryTimeout} elapses circuit breaker will
     * automatically close connection.
     *
     * @param breakerRetryTimeout timeout used by circuit
     * @param breakerRetryTimeUnit timeout time unit
     * @return {@code ExecutorCommand<V>} executor with configured timeout
     */
    public ExecutorCommand<V> withBreakerRetryTimeout(long breakerRetryTimeout, TimeUnit breakerRetryTimeUnit) {
        config.setBreakerRetryTimeout(breakerRetryTimeUnit.toMillis(breakerRetryTimeout));
        return this;
    }

    /**
     * Invokes runnable synchronously with respecting circuit logic and cache
     * logic if configured.
     *
     * @param runnable method fired by executor
     * @throws Exception if runnable throws some exception
     */
    public void invoke(CheckedRunnable runnable) throws Exception {
        invoke(()
                -> {
                    runnable.run();
                    return null;
                });
    }

    /**
     * Invokes callable synchronously with respecting circuit logic and cache
     * logic if configured.
     *
     * @param <V> type of value returned by this method
     * @param callable method fired by executor
     * @return {@code V} returns value returned by callable
     * @throws Exception if callable throws some exception
     */
    public <V> V invoke(Callable<V> callable) throws Exception {
        return service.executeSynchronously(callable, config);
    }

    /**
     * Invokes runnable asynchronously with respecting circuit logic and cache
     * logic if configured.
     *
     * @param runnable method fired by executor
     */
    public void queue(CheckedRunnable runnable) {
        queue(()
                -> {
                    runnable.run();
                    return null;
                });
    }

    /**
     * Invokes runnable asynchronously with respecting circuit logic and cache
     * logic if configured.
     *
     * @param callable method fired by executor
     * @return {@code Future<V>} with value or exception returned by callable
     */
    public Future<V> queue(Callable<V> callable) {
        return service.executeAsynchronously(callable, config);
    }

    /**
     * Invokes runnable asynchronously with respecting circuit logic and cache
     * logic if configured. If callable completed with success then the
     * {@code onSuccess} method is called.
     *
     * @param <V> type of returned value by callable
     * @param callable method fired by executor
     * @param onSuccess method fired if callable is completed with success
     */
    public <V> void observe(Callable<V> callable, CheckedConsumer<V> onSuccess) {
        service.executeAsynchronously(wrap(callable, onSuccess), config);
    }

    /**
     * Invokes runnable asynchronously with respecting circuit logic and cache
     * logic if configured. If callable completed with success then the
     * {@code onSuccess} method is called.
     *
     * @param runnable method fired by executor
     * @param onSuccess method fired if callable is completed with success
     */
    public void observe(CheckedRunnable runnable, CheckedRunnable onSuccess) {
        service.executeAsynchronously(wrap(runnable, onSuccess), config);
    }

    /**
     * Invokes runnable asynchronously with respecting circuit logic and cache
     * logic if configured. If callable completed with success then the
     * {@code onSuccess} method is called. If callable throws exception then
     * {@code onFailure} method is called
     *
     * @param <V> type of returned value by callable
     * @param callable method fired by executor
     * @param onSuccess method fired if callable is completed with success
     * @param onFailure method fired if callable throws
     */
    public <V> void observe(Callable<V> callable, CheckedConsumer<V> onSuccess, Consumer<Exception> onFailure) {
        service.executeAsynchronously(wrap(callable, onSuccess, onFailure), config);
    }

    /**
     * Invokes runnable asynchronously with respecting circuit logic and cache
     * logic if configured. If callable completed with success then the
     * {@code onSuccess} method is called. If callable throws exception then
     * {@code onFailure} method is called
     *
     * @param runnable method fired by executor
     * @param onSuccess method fired if callable is completed with success
     * @param onFailure method fired if callable throws
     */
    public void observe(CheckedRunnable runnable, CheckedRunnable onSuccess, Consumer<Exception> onFailure) {
        service.executeAsynchronously(wrap(runnable, onSuccess, onFailure), config);
    }

    private <V> Callable<V> wrap(Callable<V> callable, CheckedConsumer<V> successConsumer) {
        return wrap(callable, successConsumer, (ex) -> LOGGER.debug("Exception thrown by observed callable", ex));
    }

    private <V> Callable<V> wrap(CheckedRunnable runnable, CheckedRunnable onSuccess) {
        return wrap(runnable, onSuccess, (ex) -> LOGGER.debug("Exception thrown by observed callable", ex));
    }

    private <V> Callable<V> wrap(CheckedRunnable runnable, CheckedRunnable onSuccess, Consumer<Exception> errorConsumer) {
        return () -> {
            try {
                runnable.run();
                try {
                    onSuccess.run();
                } catch (Exception ex) {
                    LOGGER.debug("Suppress exception throwed by success callback", ex);
                }
            } catch (Exception ex) {
                errorConsumer.accept(ex);
            }
            return null;
        };
    }

    private <V> Callable<V> wrap(Callable<V> callable, CheckedConsumer<V> successConsumer, Consumer<Exception> errorConsumer) {
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

    /**
     * Enables exception suppressing mode.
     *
     * @return {@code SilentFailExecutorCommand} with exception suppressing
     */
    public SilentFailExecutorCommand<V> silentFailMode() {
        return new SilentFailExecutorCommand<>(config);
    }

    /**
     * Creates new executor with circuit breaker with name {@code circuitName}
     * used to determine if circuit is closed or open by another executors.
     *
     * @param <V> type returned by callable methods
     * @param circuitName used to enable circuit breaker
     * @return {@code ExecutorCommand<V>}
     */
    public static <V> ExecutorCommand<V> executor(String circuitName) {
        CommandConfig commandConfig = new CommandConfig();
        commandConfig.setCommandName(circuitName);
        return new ExecutorCommand<>(commandConfig);
    }

}
