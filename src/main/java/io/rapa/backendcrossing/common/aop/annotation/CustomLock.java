package io.rapa.backendcrossing.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Key;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomLock {
    Key key();
    long waitTime() default 2000L;
    long leaseTime() default 5000L;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    enum Key{
        STOCK
    }
}
