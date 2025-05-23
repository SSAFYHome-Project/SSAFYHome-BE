package com.ssafyhome.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException e) {
        log.error("비즈니스 예외 발생: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        log.error("인증 오류: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("리소스를 찾을 수 없습니다: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<?> handleJpaEntityNotFoundException(jakarta.persistence.EntityNotFoundException e) {
        log.error("엔티티를 찾을 수 없습니다: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "요청한 데이터를 찾을 수 없습니다.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("커스텀 엔티티 예외: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        log.error("비즈니스 로직 예외: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        log.error("API 예외: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();

        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("접근 거부 예외: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "접근 권한이 없습니다.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<?> handleValidationExceptions(Exception e) {
        log.error("입력값 검증 실패: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "입력값이 유효하지 않습니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("요청 파라미터 타입 불일치: {}", e.getMessage());
        String message = String.format("파라미터 '%s'의 값이 올바르지 않습니다: %s", e.getName(), e.getValue());

        Map<String, Object> response = new HashMap<>();
        response.put("error", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("지원하지 않는 HTTP 메서드: {}", e.getMethod());
        String message = String.format("지원하지 않는 HTTP 메서드입니다: %s", e.getMethod());

        Map<String, Object> response = new HashMap<>();
        response.put("error", message);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("지원하지 않는 미디어 타입: {}", e.getMessage());
        String message = String.format("지원하지 않는 미디어 타입입니다: %s", e.getContentType());

        Map<String, Object> response = new HashMap<>();
        response.put("error", message);

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("업로드 파일 용량 초과: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "업로드 가능한 파일 크기를 초과했습니다.");

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception e) {
        log.error("예상치 못한 서버 오류 발생", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

