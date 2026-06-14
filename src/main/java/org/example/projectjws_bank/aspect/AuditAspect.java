package org.example.projectjws_bank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @AfterReturning(
            pointcut = "execution(* org.example.projectjws_bank.service.AccountService.transfer(..))",
            returning = "result"
    )
    public void logTransferSuccess(JoinPoint joinPoint, Object result) {

        Object[] args = joinPoint.getArgs();

        String from = (String) args[0];
        String to = (String) args[1];
        BigDecimal amount = (BigDecimal) args[2];

        log.info("[AUDIT SUCCESS] Transfer {} from {} to {}", amount, from, to);
    }

    @AfterThrowing(
            pointcut = "execution(* org.example.projectjws_bank.service.AccountService.transfer(..))",
            throwing = "ex"
    )
    public void logTransferFail(JoinPoint joinPoint, Exception ex) {

        Object[] args = joinPoint.getArgs();

        String from = (String) args[0];
        String to = (String) args[1];
        BigDecimal amount = (BigDecimal) args[2];

        log.error("[AUDIT FAIL] Transfer {} from {} to {} | reason: {}",
                amount, from, to, ex.getMessage());
    }
}