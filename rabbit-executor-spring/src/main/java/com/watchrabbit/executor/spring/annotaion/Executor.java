package com.watchrabbit.executor.spring.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Mariusz
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Executor {

    String circuitName();

    long breakerRetryTimeout() default 100;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
