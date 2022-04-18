package com.example.tutorialBackend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    /**
     * 재시도 횟수
     */
    int retry() default 1;

    /**
     * 재시도 지연시간
     */
    long delay() default 500L;
}
