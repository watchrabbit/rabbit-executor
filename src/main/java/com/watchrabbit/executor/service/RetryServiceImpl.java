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

import com.watchrabbit.executor.wrapper.RetryConfig;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class RetryServiceImpl implements RetryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryServiceImpl.class);

    @Override
    public <V> Callable<V> addRetry(Callable<V> callable, ExecutorService pool, RetryConfig retryConfig) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception ex) {
                LOGGER.debug("Error on callable invoking, will sleep until next execution", ex);
                return Executors.newSingleThreadScheduledExecutor()
                        .schedule(callable, retryConfig.getRetryInterval(), TimeUnit.MILLISECONDS)
                        .get();
            }
        };
    }

}
