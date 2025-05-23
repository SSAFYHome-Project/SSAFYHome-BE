package com.ssafyhome.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafyhome.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * API 응답의 표준 형식을 정의하는 클래스
 * 기존 응답 형식과의 호환성을 유지하면서 점진적으로 개선
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에 포함하지 않음
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorResponse error;

    /**
     * 성공 응답 생성 (데이터 포함)
     * 기존 응답 형식과 호환되도록 데이터를 그대로 반환
     */
    public static <T> T successLegacy(T data) {
        return data;
    }

    /**
     * 성공 응답 생성 (메시지와 데이터를 Map으로 포함)
     * 기존 컨트롤러에서 Map을 반환하는 경우와 호환
     */
    public static Map<String, Object> successLegacyMap(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        if (data != null) {
            if (data instanceof Map) {
                response.putAll((Map<String, Object>) data);
            } else {
                response.put("data", data);
            }
        }
        return response;
    }

    /**
     * 성공 응답 생성 (데이터 포함)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data, null);
    }

    /**
     * 성공 응답 생성 (메시지와 데이터 포함)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", null, null);
    }

    /**
     * 오류 응답 생성 (메시지만 포함)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    /**
     * 오류 응답 생성 (ErrorResponse 포함)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(false, errorResponse.getMessage(), null, errorResponse);
    }

    /**
     * 오류 응답 생성 (ErrorCode 기반)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ApiResponse<>(false, errorCode.getMessage(), null, errorResponse);
    }

    /**
     * 오류 응답 생성 (ErrorCode와 메시지 기반)
     * 새로운 응답 형식 사용
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);
        return new ApiResponse<>(false, message, null, errorResponse);
    }

    /**
     * 기존 오류 응답 형식과 호환되는 Map 반환
     */
    public static Map<String, Object> errorLegacyMap(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}
