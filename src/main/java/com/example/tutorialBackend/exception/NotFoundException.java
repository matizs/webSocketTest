package com.example.tutorialBackend.exception;

import java.util.Map;
import java.util.function.Supplier;

import com.example.tutorialBackend.common.ErrorType;

public class NotFoundException extends RuntimeException implements Supplier<RuntimeException> {
    private ErrorType errorType;

    public NotFoundException(ErrorType errorType) {
        this.errorType = errorType;
    }

    @Override
    public RuntimeException get() {
        return this;
    }

    public Map getErrorInfo() {
        return errorType.getInfo();
    }
}
