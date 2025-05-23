package com.ssafyhome.common.exception;

/**
 * 인증 관련 예외를 처리하는 클래스
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

