package com.ssafyhome.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    // Authentication
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "인증에 실패했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 올바르지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U004", "비밀번호가 일치하지 않습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "U005", "이메일 형식이 올바르지 않습니다."),

    // API
    API_CALL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API001", "외부 API 호출에 실패했습니다."),
    API_RESPONSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API002", "API 응답 처리 중 오류가 발생했습니다."),
    API_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "API003", "API 요청 시간이 초과되었습니다."),

    // Redis
    REDIS_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "R001", "Redis 연결에 실패했습니다."),
    CACHE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "R002", "캐시 처리 중 오류가 발생했습니다."),

    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "게시글을 찾을 수 없습니다."),
    NOT_BOARD_OWNER(HttpStatus.FORBIDDEN, "B002", "게시글 작성자만 수정/삭제할 수 있습니다."),
    BOARD_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "B003", "이미 삭제된 게시글입니다."),

    // Reply
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "댓글을 찾을 수 없습니다."),
    NOT_REPLY_OWNER(HttpStatus.FORBIDDEN, "R002", "댓글 작성자만 수정/삭제할 수 있습니다."),

    // Bookmark
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BM001", "이미 북마크에 추가된 항목입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BM002", "북마크를 찾을 수 없습니다."),

    // File
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F002", "파일 크기가 제한을 초과했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F003", "지원하지 않는 파일 형식입니다."),

    // Map
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "위치 정보를 찾을 수 없습니다."),
    INVALID_COORDINATES(HttpStatus.BAD_REQUEST, "M002", "올바르지 않은 좌표입니다."),

    // Deal
    DEAL_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "거래 정보를 찾을 수 없습니다."),

    // Email
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "이메일 전송에 실패했습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "E002", "인증번호가 올바르지 않습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "E003", "인증번호가 만료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
