package io.rapa.backendcrossing.common.aop.aspect;

import io.rapa.backendcrossing.common.aop.AopTransactionImpl;
import io.rapa.backendcrossing.common.aop.AopTransactionManager;
import io.rapa.backendcrossing.common.aop.annotation.CustomLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
    private final AopTransactionManager aopTransactionManager;
    private final RedissonClient redissonClient;
    @Around("@annotation(io.rapa.backendcrossing.common.aop.annotation.CustomLock) && @annotation(customLock)")
    public Object lock(ProceedingJoinPoint proceedingJoinPoint, CustomLock customLock) throws Throwable{
        Long targetId = (Long) proceedingJoinPoint.getArgs()[1];
        RLock rLock = redissonClient.getLock("%s:%d".formatted(customLock.key(), targetId));
        try{
            // 락 획득 실행
            Boolean isAvailable = rLock.tryLock(customLock.waitTime(),customLock.leaseTime(), TimeUnit.MILLISECONDS);
            // 락 획득 실패 시 중단
            if( !isAvailable ) return false;
            return aopTransactionManager.proceed(proceedingJoinPoint);
        } finally{
            // 메서드 실행이 완료된 경우 unlock 실행
            if(rLock.isHeldByCurrentThread()) rLock.unlock();
        }
    }

}
