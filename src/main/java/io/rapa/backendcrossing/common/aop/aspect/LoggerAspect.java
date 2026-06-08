package io.rapa.backendcrossing.common.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggerAspect {
    @Around("@annotation(io.rapa.backendcrossing.common.aop.annotation.SpecLogger)")
    public Object findExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTimeMilis = System.currentTimeMillis();
        Object returnValue = proceedingJoinPoint.proceed();
        long endTimeMilis = System.currentTimeMillis();
        log.info("@Around 적용 Aspect - {} Method 실행시간 {} ms", proceedingJoinPoint , endTimeMilis - startTimeMilis);
        return returnValue;
    }
}
