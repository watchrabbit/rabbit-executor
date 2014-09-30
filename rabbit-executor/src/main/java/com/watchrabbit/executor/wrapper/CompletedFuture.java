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
package com.watchrabbit.executor.wrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class CompletedFuture<V> implements Future<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletedFuture.class);

    private final Throwable exception;

    private final V value;

    public CompletedFuture(Throwable exception) {
        this.exception = exception;
        this.value = null;
    }

    public CompletedFuture(V value) {
        this.exception = null;
        this.value = value;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        LOGGER.debug("Can't cancel CompletedFuture");
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return value;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }

}
