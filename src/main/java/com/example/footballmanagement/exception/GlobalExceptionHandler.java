package com.example.footballmanagement.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorCode code = ex.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .message(code.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(code.getStatus()).body(response);
    }

    // fallback cho lỗi chưa handle
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .status(500)
                .error("Internal Server Error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("⚠️ Trùng lịch bảo trì hoặc dữ liệu không hợp lệ, vui lòng kiểm tra lại.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}
