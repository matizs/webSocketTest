package com.example.tutorialBackend.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.tutorialBackend.exception.NotFoundException;

@Aspect
@Component
public class ExistCheckAspect {

    @Around("@annotation(com.example.tutorialBackend.common.annotation.ExistCheck)")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        Object ret = null;
        try{
            ret = proceedingJoinPoint.proceed(args);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getErrorInfo());
        }
        return ret;
    }
}
