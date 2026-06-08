package io.rapa.backendcrossing.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AopTransactionImpl implements AopTransactionManager{
    @Override
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint;
    }
}
