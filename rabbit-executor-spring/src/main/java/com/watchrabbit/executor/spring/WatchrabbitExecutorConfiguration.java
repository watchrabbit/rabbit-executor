package com.watchrabbit.executor.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 *
 * @author Mariusz
 */
@Configuration
public class WatchrabbitExecutorConfiguration {

    @Bean(name = "com.watchrabbit.executor.spring.internalWatchrabbitExecutorConfiguration")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ExecutorAnnotationBeanPostProcessor executorAnnotationBeanPostProcessor() {
        return new ExecutorAnnotationBeanPostProcessor();
    }
}
