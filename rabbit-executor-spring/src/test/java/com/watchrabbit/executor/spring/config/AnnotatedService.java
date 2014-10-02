package com.watchrabbit.executor.spring.config;

import com.watchrabbit.executor.spring.annotaion.Executor;
import java.util.concurrent.Callable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mariusz
 */
@Service
public class AnnotatedService {

    @Executor(circuitName = "helloWorld")
    public String helloWorld(Callable<String> callable) throws Exception {
        return callable.call();
    }

    @Executor(circuitName = "fastClose", breakerRetryTimeout = 1)
    public String fastClose(Callable<String> callable) throws Exception {
        return callable.call();
    }
}
