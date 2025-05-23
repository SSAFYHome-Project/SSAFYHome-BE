package com.ssafyhome.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 애플리케이션 전반의 로깅을 담당하는 AOP 클래스
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * 컨트롤러 클래스에 대한 포인트컷
     */
    @Pointcut("execution(* com.ssafyhome..controller..*.*(..))")
    public void controllerPointcut() {}

    /**
     * 서비스 클래스에 대한 포인트컷
     */
    @Pointcut("execution(* com.ssafyhome..service..*.*(..))")
    public void servicePointcut() {}

    /**
     * API 클라이언트 클래스에 대한 포인트컷
     */
    @Pointcut("execution(* com.ssafyhome..api..*.*(..))")
    public void apiClientPointcut() {}

    /**
     * 컨트롤러 메소드 실행 시간 로깅
     */
    @Around("controllerPointcut()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "Controller");
    }

    /**
     * 서비스 메소드 실행 시간 로깅
     */
    @Around("servicePointcut()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "Service");
    }

    /**
     * API 클라이언트 메소드 실행 시간 로깅
     */
    @Around("apiClientPointcut()")
    public Object logApiClientExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "API Client");
    }

    /**
     * 메소드 실행 시간 로깅 공통 로직
     */
    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            if (executionTime > 1000) { // 1초 이상 걸리는 메소드는 WARN 레벨로 로깅
                log.warn("{} - {}.{} 실행 시간: {}ms", type, className, methodName, executionTime);
            } else if (log.isDebugEnabled()) {
                log.debug("{} - {}.{} 실행 시간: {}ms", type, className, methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("{} - {}.{} 실행 중 예외 발생 ({}ms): {}",
                    type, className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * 예외 발생 시 상세 로깅
     */
    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut() || apiClientPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.error("예외 발생: {}.{} - 예외 타입: {}, 메시지: {}",
                className, methodName, exception.getClass().getName(), exception.getMessage());

        if (log.isDebugEnabled()) {
            log.debug("메소드 인자: {}", Arrays.toString(args));
            log.debug("스택 트레이스:", exception);
        }
    }
}
