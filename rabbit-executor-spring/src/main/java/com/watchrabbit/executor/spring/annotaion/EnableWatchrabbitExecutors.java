package com.watchrabbit.executor.spring.annotaion;

import com.watchrabbit.executor.spring.WatchrabbitExecutorConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Mariusz
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WatchrabbitExecutorConfiguration.class)
@Documented
public @interface EnableWatchrabbitExecutors {

}
