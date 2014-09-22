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
import com.watchrabbit.executor.service.CommandService;
import com.watchrabbit.executor.service.CommandServiceImpl;
import com.watchrabbit.executor.wrapper.CheckedRunnable;
import com.watchrabbit.executor.wrapper.CommandConfigWrapper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

    private CommandConfigWrapper config = new CommandConfigWrapper();

    protected ExecutorCommand() {
    }

    protected ExecutorCommand(CommandConfigWrapper config) {
        this.config = config;
    }

    public ExecutorCommand withCommandName(String name) {
        this.config.setCommandName(name);
        return this;
    }

    public void invoke(CheckedRunnable runnable) throws ExecutionException {
        invoke(()
                -> {
                    runnable.run();
                    return null;
                });
    }

    public V invoke(Callable<V> callable) throws ExecutionException {
        try {
            return service.executeSynchronously(callable, config).get();
        } catch (InterruptedException ex) {
            LOGGER.error("Shoudl never be thrown!", ex);
            throw new SystemException("Shoudl never be thrown!", ex);
        }
    }

    public void queue(CheckedRunnable runnable) {
        queue(()
                -> {
                    runnable.run();
                    return null;
                });
    }

    public Future<V> queue(Callable<V> callable) {
        return service.executeAsynchronously(callable, config);
    }

    public void observe(Callable<V> callable, CheckedConsumer<V> onSuccess) {
        service.executeAsynchronously(wrap(callable, onSuccess), config);
    }

    public void observe(CheckedRunnable runnable, CheckedRunnable onSuccess) {
        service.executeAsynchronously(wrap(runnable, onSuccess), config);
    }

    public void observe(Callable<V> callable, CheckedConsumer<V> onSuccess, Consumer<Exception> onFailure) {
        service.executeAsynchronously(wrap(callable, onSuccess, onFailure), config);
    }

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

    public SilentFailExecutorCommand<V> silentFailMode() {
        return new SilentFailExecutorCommand<>(config);
    }

    public static <V> ExecutorCommand<V> executor(String circutName) {
        return new ExecutorCommand<>().withCommandName(circutName);
    }

}
