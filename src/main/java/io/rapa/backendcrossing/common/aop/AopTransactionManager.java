package io.rapa.backendcrossing.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;

public interface AopTransactionManager {
    Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;
}
