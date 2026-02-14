package com.standalonejhgl.holoseogiapi.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // === AUTH / LOGIN ===
    INVALID_FIREBASE_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 Firebase 토큰입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_002", "사용자를 찾을 수 없습니다"),
    DUPLICATE_USER(HttpStatus.OK, "AUTH_003", "이미 가입된 사용자입니다"),
    INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_004", "정보를 조회할수 없습니다."),
    // === SUBSCRIPTION ===
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SUB_001", "구독 정보가 없습니다"),
    SUBSCRIPTION_EXPIRED(HttpStatus.OK, "SUB_002", "구독이 만료되었습니다"),

    // === FCM ===
    FCM_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "FCM_001", "FCM 토큰이 유효하지 않습니다"),

    // === COMMON ===
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COM_001", "잘못된 요청입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COM_002", "권한이 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}