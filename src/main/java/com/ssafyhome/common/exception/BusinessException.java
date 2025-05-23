package com.ssafyhome.common.exception;

/**
 * 비즈니스 로직 관련 예외를 처리하는 클래스
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
