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

import static com.watchrabbit.executor.command.ExecutorCommand.executor;
import static com.watchrabbit.executor.wrapper.CacheConfig.cache;
import java.util.concurrent.CountDownLatch;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Mariusz
 */
public class CacheTest {

    @Test
    public void shoudlInvokeOnlyOnce() {
        CountDownLatch latch = new CountDownLatch(2);

        String invoke = ExecutorCommand.<String>executor("")
                .withCache(
                        cache("name", "key")
                ).silentFailMode()
                .invoke(
                        () -> {
                            latch.countDown();
                            return "abc";
                        }
                );
        String secondInvoke = executor("")
                .withCache(
                        cache("name", "key")
                ).silentFailMode()
                .invoke(
                        () -> {
                            latch.countDown();
                            return "abc";
                        }
                );

        assertThat(latch.getCount()).isEqualTo(1);
        assertThat(invoke).isEqualTo(secondInvoke);
    }

    @Test
    public void shoudlInvokeTwiceOnDiffrentKeys() {
        CountDownLatch latch = new CountDownLatch(2);

        String invoke = ExecutorCommand.<String>executor("")
                .withCache(
                        cache("secondName", "key")
                ).silentFailMode()
                .invoke(
                        () -> {
                            latch.countDown();
                            return "abc";
                        }
                );
        String secondInvoke = executor("")
                .withCache(
                        cache("secondName", "key2")
                ).silentFailMode()
                .invoke(
                        () -> {
                            latch.countDown();
                            return "abc";
                        }
                );

        assertThat(latch.getCount()).isEqualTo(0);
        assertThat(invoke).isEqualTo(secondInvoke);
    }

    @Test
    public void shoudlInvokeTwiceOnDiffrentCaches() {
        CountDownLatch latch = new CountDownLatch(2);

        String invoke = ExecutorCommand.<String>executor("")
                .withCache(
                        cache("cache1", "key")
                ).silentFailMode()
                .invoke(
                        () -> {
                            latch.countDown();
                            return "abc";
                        }
                );
        String secondInvoke = executor("")
                .withCache(
                        cache("cache2", "key")
                ).silentFailMode()
                .invoke(
                        () -> {
                            latch.countDown();
                            return "abc";
                        }
                );

        assertThat(latch.getCount()).isEqualTo(0);
        assertThat(invoke).isEqualTo(secondInvoke);
    }
}
