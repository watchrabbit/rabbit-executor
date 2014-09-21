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
import com.watchrabbit.executor.exception.CommandNameGeneratorException;
import com.watchrabbit.executor.service.CommandService;
import com.watchrabbit.executor.service.CommandServiceImpl;
import com.watchrabbit.executor.wrapper.CheckedRunnable;
import com.watchrabbit.executor.wrapper.CommandConfigWrapper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class ExecutorCommand<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorCommand.class);

    private final CommandService service = new CommandServiceImpl();

    private final CommandConfigWrapper config = new CommandConfigWrapper();

    private ExecutorCommand() {

    }

    public ExecutorCommand withCommandName(String name) {
        this.config.setCommandName(name);
        return this;
    }

    public ExecutorCommand withDedicatedThreadPool() {
        this.config.setDedicatedThreadPool(true);
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
        init();
        try {
            return service.executeSynchronously(callable, config).get();
        } catch (InterruptedException ex) {
            LOGGER.error("Shoudl never be thrown!", ex);
            throw new SystemException("Shoudl never be thrown!", ex);
        }
    }

    public Future<V> queue(Callable<V> callable) {
        init();
        return service.executeAsynchronously(callable, config);
    }

    public void observe(Callable<V> callable, CheckedConsumer<V> onSuccess) {
        init();
        service.executeAsynchronously(wrap(callable, onSuccess), config);
    }

    public void observe(Callable<V> callable, CheckedConsumer<V> onSuccess, Consumer<Exception> onFailure) {
        init();
        service.executeAsynchronously(wrap(callable, onSuccess, onFailure), config);
    }

    private synchronized void init() {
        if (StringUtils.isBlank(config.getCommandName())) {
            try {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace.length < 4) {
                    throw new CommandNameGeneratorException("Cannot auto generate command name");
                }
                String generatedName = new StringBuilder(stackTrace[3].getClassName())
                        .append(":")
                        .append(stackTrace[3].getMethodName())
                        .toString();
                config.setCommandName(generatedName);
            } catch (SecurityException ex) {
                throw new CommandNameGeneratorException("Cannot auto generate command name", ex);
            }
        }
    }

    private <V> Callable<V> wrap(Callable<V> callable, CheckedConsumer<V> successConsumer) {
        return wrap(callable, successConsumer, (ex) -> LOGGER.debug("Exception thrown by observed callable", ex));
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

    public static <V> ExecutorCommand<V> executor() {
        return new ExecutorCommand<>();
    }

}
