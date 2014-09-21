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

import com.watchrabbit.commons.marker.Todo;
import com.watchrabbit.executor.wrapper.CircutBreaker;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Mariusz
 */
public class CircutBreakerService {

    private static final ConcurrentHashMap<String, CircutBreaker> circutBreakers = new ConcurrentHashMap<>();

    public synchronized <V> Callable<V> addCircutBreaker(Callable<V> callable, String commandName) {
        if (!circutBreakers.containsKey(commandName)) {
            circutBreakers.put(commandName, new CircutBreaker(commandName));
        }
        CircutBreaker breaker = circutBreakers.get(commandName);
        return wrap(callable, breaker);
    }

    @Todo
    private <V> Callable<V> wrap(Callable<V> callable, CircutBreaker breaker) {
        return () -> {
            return callable.call();
        };
    }

}
