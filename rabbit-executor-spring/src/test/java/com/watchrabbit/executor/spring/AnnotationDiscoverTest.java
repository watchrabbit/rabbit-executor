package com.watchrabbit.executor.spring;

import com.watchrabbit.commons.sleep.Sleep;
import com.watchrabbit.executor.exception.CircuitOpenException;
import com.watchrabbit.executor.spring.config.AnnotatedService;
import com.watchrabbit.executor.spring.config.ContextTestBase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Mariusz
 */
public class AnnotationDiscoverTest extends ContextTestBase {

    @Autowired
    AnnotatedService annotatedService;

    @Test(expected = CircuitOpenException.class)
    public void shouldCallOnlyOnce() throws Exception {
        try {
            annotatedService.helloWorld(() -> {
                throw new Exception();
            });
            failBecauseExceptionWasNotThrown(Exception.class);
        } catch (Exception ex) {
        }
        annotatedService.helloWorld(() -> {
            throw new Exception();
        });
        failBecauseExceptionWasNotThrown(Exception.class);
    }

    @Test
    public void shouldCloseCircuit() throws Exception {
        try {
            annotatedService.fastClose(() -> {
                throw new Exception();
            });
            failBecauseExceptionWasNotThrown(Exception.class);
        } catch (Exception ex) {
        }
        Sleep.untilTrue(Boolean.TRUE::booleanValue, 10, TimeUnit.MILLISECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        annotatedService.fastClose(() -> {
            latch.countDown();
            return "ok";
        });
        assertThat(latch.getCount()).isEqualTo(0);
    }
}
