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

/**
 *
 * @author Mariusz
 */
public class CacheConfig {

    private final String cacheName;

    private final String key;

    private int cacheSize = 1000;

    private int expireTime = 60;

    private CacheConfig(String cacheName, String key) {
        this.cacheName = cacheName;
        this.key = key;
    }

    public CacheConfig withCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    public CacheConfig withExpireTime(int expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public String getKey() {
        return key;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public String getCacheName() {
        return cacheName;
    }

    public static CacheConfig cache(String cacheName, String key) {
        return new CacheConfig(cacheName, key);
    }
}
