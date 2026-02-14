

package com.standalonejhgl.holoseogiapi.exception;
import java.sql.SQLException;

import com.standalonejhgl.holoseogiapi.dtos.common.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NoResourceFoundException e) {

        String path = e.getResourcePath() == null ? "" : e.getResourcePath();

        //봇 스캔 로그 기록 방지용
        if (isBotNoisePath(path)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto(false, "존재하지 않는 경로입니다.", "404"));
        }

        if (isSuspiciousPath(path)) {
            log.warn("SUSPICIOUS_404 path={}", path);
        } else {
            log.info("NOT_FOUND path={}", path);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(false, "존재하지 않는 경로입니다.", "404"));
    }

    private boolean isBotNoisePath(String path) {
        if (path == null) return true;

        String p = path.toLowerCase();

        if (p.equals("/favicon.ico") ||
                p.equals("/robots.txt") ||
                p.equals("/sitemap.xml") ||
                p.equals("/apple-touch-icon.png") ||
                p.equals("/apple-touch-icon-precomposed.png")) {
            return true;
        }

        return p.contains("wp-admin")
                || p.contains("wp-login")
                || p.contains("wordpress")
                || p.contains("phpmyadmin")
                || p.contains("pma")
                || p.contains(".well-known")
                || p.contains("cgi-bin");
    }

    private boolean isSuspiciousPath(String path) {
        if (path == null) return true;

        String p = path.toLowerCase();

        // ✅ 경로침투 / 민감파일 스캔
        return p.contains("..")
                || p.contains("%2e%2e")
                || p.contains("%2f")
                || p.contains("\\")
                || p.contains(".env")
                || p.contains(".git")
                || p.contains("id_rsa")
                || p.contains("config")
                || p.contains("secrets")
                || p.contains("backup")
                || p.contains("dump")
                || p.contains("swagger")
                || p.contains("api-docs");
    }

    // INTERNAL_SERVER_ERROR 용
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception e) {

        log.error("INTERNAL_SERVER_ERROR", e);

        ErrorResponseDto response = new ErrorResponseDto(false, "서버 오류 발생", "500");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // BAD REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException e) {

        log.error("BAD_REQUEST", e);

        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        ErrorResponseDto response = new ErrorResponseDto(false, message, "BAD_REQUEST");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponseDto> handleSqlException(SQLException e) {
        log.error("SQL_ERROR", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(false, "데이터 처리 중 오류 발생", "SQL_ERROR"));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e) {

        ErrorCode errorCode = e.getErrorCode();

        log.warn("BUSINESS_EXCEPTION [{}] {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponseDto(
                        false,
                        errorCode.getMessage(),
                        errorCode.getCode()
                ));
    }
}
