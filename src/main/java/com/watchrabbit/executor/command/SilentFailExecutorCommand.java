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

import com.watchrabbit.executor.wrapper.CheckedRunnable;
import com.watchrabbit.executor.wrapper.CommandConfig;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class SilentFailExecutorCommand<V> extends ExecutorCommand<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SilentFailExecutorCommand.class);

    protected SilentFailExecutorCommand(CommandConfig config) {
        super(config);
    }

    @Override
    public <V> V invoke(Callable<V> callable) {
        try {
            return super.invoke(callable);
        } catch (ExecutionException ex) {
            LOGGER.debug("Catched exception, suppressing, returning null", ex);
            return null;
        }
    }

    @Override
    public <V> void invoke(CheckedRunnable runnable) {
        try {
            super.invoke(runnable);
        } catch (ExecutionException ex) {
            LOGGER.debug("Catched exception, suppressing", ex);
        }
    }

}
