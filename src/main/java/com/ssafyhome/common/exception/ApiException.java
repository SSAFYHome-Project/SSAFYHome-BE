package com.ssafyhome.common.exception;

public class ApiException extends BaseException {

    public ApiException(String message) {
        super(ErrorCode.API_CALL_ERROR, message);
    }

    public ApiException(String message, Throwable cause) {
        super(ErrorCode.API_CALL_ERROR, message, cause);
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
