package com.standalonejhgl.holoseogiapi.dtos.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오류 응답 DTO")
public class ErrorResponseDto {

    @Schema(description = "응답 여부", example = "false")
    private boolean success;

    @Schema(description = "메세지", example = "서버 오류 발생")
    private String message;

    @Schema(description = "오류 코드", example = "500")
    private String errorCode;

    public ErrorResponseDto(boolean success, String message, String errorCode) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
}