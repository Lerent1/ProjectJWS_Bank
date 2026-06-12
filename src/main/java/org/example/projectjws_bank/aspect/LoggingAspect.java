package org.example.projectjws_bank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* org.example.projectjws_bank.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();

        log.info("START: {}", methodName);

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        log.info("END: {} | Time: {} ms", methodName, (end - start));

        return result;
    }
}