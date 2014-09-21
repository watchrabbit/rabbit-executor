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

import com.watchrabbit.executor.exception.CircutOpenException;
import com.watchrabbit.executor.wrapper.CircutBreaker;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class CircutBreakerServiceImpl implements CircutBreakerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircutBreakerServiceImpl.class);

    private static final ConcurrentHashMap<String, CircutBreaker> circutBreakers = new ConcurrentHashMap<>();

    @Override
    public synchronized <V> Callable<V> addCircutBreaker(Callable<V> callable, String commandName) {
        if (!circutBreakers.containsKey(commandName)) {
            circutBreakers.put(commandName, new CircutBreaker(commandName));
        }
        CircutBreaker breaker = circutBreakers.get(commandName);
        return wrap(callable, breaker);
    }

    private <V> Callable<V> wrap(Callable<V> callable, CircutBreaker breaker) {
        return () -> {
            if (!breaker.isClosed()) {
                LOGGER.debug("Circut is open, skipping command {} execution and throwing exception", breaker.getCommandName());
                throw new CircutOpenException();
            } else {
                LOGGER.debug("Circut is closed, executing command: {}", breaker.getCommandName());
                try {
                    return callable.call();
                } catch (Exception ex) {
                    breaker.open();
                    throw ex;
                }
            }
        };
    }

}
