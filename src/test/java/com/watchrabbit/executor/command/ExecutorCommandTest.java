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

import com.watchrabbit.commons.exception.SystemException;
import static com.watchrabbit.executor.command.ExecutorCommand.executor;
import com.watchrabbit.executor.wrapper.CheckedRunnable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Mariusz
 */
public class ExecutorCommandTest {

    @Test
    public void shoudlInvokeMethod() {
        CountDownLatch latch = new CountDownLatch(1);
        executor("")
                .silentFail()
                .invoke(()
                        -> latch.countDown()
                );

        assertThat(latch.getCount()).isEqualTo(0);
    }

    @Test
    public void shoudlBreakCircut() {
        CountDownLatch latch = new CountDownLatch(1);

        executor("config")
                .silentFail()
                .invoke((CheckedRunnable) () -> {
                    throw new SystemException();
                });

        executor("config")
                .silentFail()
                .invoke(()
                        -> latch.countDown()
                );

        assertThat(latch.getCount()).isEqualTo(1);
    }

    @Test
    public void shoudlInvokeMethodAsynchronously() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        executor("")
                .silentFail()
                .queue(()
                        -> latch.countDown()
                );

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount()).isEqualTo(0);
    }

    @Test
    public void shoudlCallOnSuccessMethod() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        executor("")
                .silentFail()
                .observe(() -> latch.countDown(),
                        () -> latch.countDown()
                );

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount()).isEqualTo(0);
    }
}
