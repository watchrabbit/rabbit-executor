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

import java.util.List;

/**
 *
 * @author Mariusz
 */
public class CommandConfig {

    private String commandName = "";

    private long breakerRetryTimeout = 100;

    private List<Class<? extends Exception>> excludedExceptions;

    private CacheConfig cacheConfig;

    private RetryConfig retryConfig;

    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public void setRetryConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public long getBreakerRetryTimeout() {
        return breakerRetryTimeout;
    }

    public void setBreakerRetryTimeout(long breakerRetryTimeout) {
        this.breakerRetryTimeout = breakerRetryTimeout;
    }

    public List<Class<? extends Exception>> getExcludedExceptions() {
        return excludedExceptions;
    }

    public void setExcludedExceptions(List<Class<? extends Exception>> excludedExceptions) {
        this.excludedExceptions = excludedExceptions;
    }

    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public void setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

}
