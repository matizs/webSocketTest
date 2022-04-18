package com.example.tutorialBackend.common.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.example.tutorialBackend.common.annotation.Retry;

@Aspect
@Component
public class RetryAspect {

    @Around("@annotation(com.example.tutorialBackend.common.annotation.Retry)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Method method = ClassUtils.getMethodIfAvailable(joinPoint.getTarget().getClass(), methodName, signature.getParameterTypes());

        Retry annotation = method.getAnnotation(Retry.class);

        Object[] args = joinPoint.getArgs();
        Object ret = null;

        for (int i = 0; i < annotation.retry(); i++) {
            try {
                ret = joinPoint.proceed(args);
                break;
            } catch (Exception e) {
                if (i < annotation.retry()) {
                    Thread.sleep(annotation.delay());
                    continue;
                } else {
                    throw e;
                }
            }
        }
        return ret;
    }
}
