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

import com.watchrabbit.executor.exception.CircuitOpenException;
import com.watchrabbit.executor.wrapper.CircuitBreaker;
import com.watchrabbit.executor.wrapper.CommandConfig;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class CircuitBreakerServiceImpl implements CircuitBreakerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerServiceImpl.class);

    private static final ConcurrentHashMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    @Override
    public <V> Callable<V> addCircuitBreaker(Callable<V> callable, CommandConfig commandConfig) {
        if (!circuitBreakers.containsKey(commandConfig.getCommandName())) {
            createCircuitBreaker(commandConfig);
        }
        CircuitBreaker breaker = circuitBreakers.get(commandConfig.getCommandName());
        return wrap(callable, breaker, commandConfig);
    }

    private static synchronized void createCircuitBreaker(CommandConfig commandConfig) {
        if (!circuitBreakers.containsKey(commandConfig.getCommandName())) {
            CircuitBreaker circuitBreaker = new CircuitBreaker(commandConfig.getBreakerRetryTimeout());
            circuitBreaker.setCommandName(commandConfig.getCommandName());
            circuitBreakers.put(commandConfig.getCommandName(), circuitBreaker);
        }
    }

    private <V> Callable<V> wrap(Callable<V> callable, CircuitBreaker breaker, CommandConfig commandConfig) {
        List<Class<? extends Exception>> excludedExceptions = commandConfig.getExcludedExceptions();
        if (excludedExceptions == null) {
            excludedExceptions = Collections.emptyList();
        }
        return wrap(callable, breaker, excludedExceptions);
    }

    private <V> Callable<V> wrap(Callable<V> callable, CircuitBreaker breaker, List<Class<? extends Exception>> excludedExceptions) {
        return () -> {
            if (!breaker.isClosed()) {
                LOGGER.debug("Circut is open, skipping command {} execution and throwing exception", breaker.getCommandName());
                throw new CircuitOpenException();
            } else {
                LOGGER.debug("Circut is closed, executing command: {}", breaker.getCommandName());
                try {
                    return callable.call();
                } catch (Exception ex) {
                    if (!excludedExceptions.contains(ex.getClass())) {
                        breaker.open();
                    }
                    throw ex;
                }
            }
        };
    }

}
