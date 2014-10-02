package com.watchrabbit.executor.spring.config;

import com.watchrabbit.executor.spring.annotaion.EnableWatchrabbitExecutors;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Mariusz
 */
@Configuration
@EnableWatchrabbitExecutors
@ComponentScan(basePackages = {"com.watchrabbit.executor.spring"}, excludeFilters = @ComponentScan.Filter({Configuration.class}))
public class TestConfiguration {

}
