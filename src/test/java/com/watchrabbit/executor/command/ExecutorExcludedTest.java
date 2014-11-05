package com.watchrabbit.executor.command;

import com.watchrabbit.commons.exception.SystemException;
import static com.watchrabbit.executor.command.ExecutorCommand.executor;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Mariusz
 */
public class ExecutorExcludedTest {

    @Test
    public void shouldIgnoreException() {
        CountDownLatch latch = new CountDownLatch(1);
        SilentFailExecutorCommand<Object> command = executor("shouldIgnoreException")
                .withExcludedExceptions(Arrays.asList(SystemException.class))
                .silentFailMode();
        command.invoke(()
                -> {
                    throw new SystemException();
                });
        command.invoke(() -> latch.countDown());

        assertThat(latch.getCount()).isEqualTo(0);
    }
}
