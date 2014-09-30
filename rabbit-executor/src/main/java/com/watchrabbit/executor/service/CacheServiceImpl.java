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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.watchrabbit.executor.wrapper.CacheConfig;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mariusz
 */
public class CacheServiceImpl implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceImpl.class);

    private static final ConcurrentHashMap<String, Cache<String, Object>> caches = new ConcurrentHashMap<>();

    @Override
    public <V> Callable<V> addCache(Callable<V> wrapped, CacheConfig cacheConfig) {
        if (!caches.containsKey(cacheConfig.getCacheName())) {
            createCache(cacheConfig);
        }
        Cache<String, Object> cache = caches.get(cacheConfig.getCacheName());
        return wrap(wrapped, cache, cacheConfig.getKey());
    }

    private static synchronized void createCache(CacheConfig cacheConfig) {
        if (!caches.containsKey(cacheConfig.getCacheName())) {
            Cache<String, Object> cache = CacheBuilder.newBuilder()
                    .maximumSize(cacheConfig.getCacheSize())
                    .expireAfterWrite(cacheConfig.getExpireTime(), TimeUnit.SECONDS)
                    .build();
            caches.put(cacheConfig.getCacheName(), cache);
        }
    }

    private <V> Callable<V> wrap(Callable<V> callable, Cache<String, Object> cache, String key) {
        return () -> {
            try {
                V value = (V) cache.getIfPresent(key);
                if (value == null) {
                    value = callable.call();
                    if (value != null) {
                        cache.put(key, value);
                    }
                }
                return value;
            } catch (ExecutionException ex) {
                LOGGER.debug("Error on cache loading", ex);
                throw ex;
            }
        };
    }
}
