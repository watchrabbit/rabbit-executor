package com.watchrabbit.executor.spring;

import java.lang.reflect.Method;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;
import org.springframework.core.Ordered;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.util.ReflectionUtils;

/**
 *
 * @author Mariusz
 */
public class ExecutorAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {

                return "Hello cglib!";

            }
        });
        enhancer.create();

        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                for (Scheduled scheduled : AnnotationUtils.getRepeatableAnnotation(method, Schedules.class, Scheduled.class)) {
//                    processScheduled(scheduled, method, bean);
                }
            }
        });
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
