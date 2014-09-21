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

import com.watchrabbit.executor.wrapper.CommandConfigWrapper;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author Mariusz
 */
public interface CommandService {

    public <V> Future<V> executeSynchronously(Callable<V> callable, CommandConfigWrapper commandWrapper);

    public <V> Future<V> executeAsynchronously(Callable<V> callable, CommandConfigWrapper commandWrapper);

}
