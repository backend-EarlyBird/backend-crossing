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
    // 해당 어노테이션이 선언된 메서드 호출 시 자동으로 식별 후 분산락 적용
    @Around("@annotation(io.rapa.backendcrossing.common.aop.annotation.CustomLock) && @annotation(customLock)")
    public Object lock(ProceedingJoinPoint proceedingJoinPoint, CustomLock customLock) throws Throwable{
        Long targetId = (Long) proceedingJoinPoint.getArgs()[1];
        RLock rLock = redissonClient.getLock("%s:%d".formatted(customLock.key(), targetId));
        try{
            // 락 획득 실행
            Boolean isAvailable = rLock.tryLock(customLock.waitTime(),customLock.leaseTime(), TimeUnit.MILLISECONDS);
            // 락 획득 실패 시 중단
            if( !isAvailable ) throw new IllegalStateException("락 획득 실패");
            // 락 획득 시 AOP에 의해 인터셉트된 메서드 실행
            return aopTransactionManager.proceed(proceedingJoinPoint);
        } finally{
            // 비즈니스 로직 수행 후 현재 스레드가 보유한 분산락을 해제
            // 로직 수행 중 예외 발생 시에도 finally 블록에서 락 반납을 보장
            if(rLock.isHeldByCurrentThread()) rLock.unlock();
        }
    }

}
