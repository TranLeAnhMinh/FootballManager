package com.example.footballmanagement.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // ===== AUTH / ROLE =====
    ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "Role is required"),
    ROLE_FORBIDDEN(HttpStatus.FORBIDDEN, "You are not allowed to perform this action"),

    // ===== USER =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "Tài khoản của bạn đang bị vô hiệu hóa"),

    // ===== PITCH / MAINTENANCE =====
    PITCH_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy sân bóng"),
    MAINTENANCE_CONFLICT(HttpStatus.CONFLICT, "Khung giờ bảo trì bị trùng"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác này"),

    // ===== BRANCH =====
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy chi nhánh"),
    MAINTENANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy lịch bảo trì"),
    MAINTENANCE_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "Không thể xoá lịch bảo trì đã hoặc đang diễn ra"),

    // ===== VOUCHER (USER FLOW) =====
    VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "Voucher not found"),
    VOUCHER_INACTIVE(HttpStatus.BAD_REQUEST, "Voucher is not active"),
    VOUCHER_NOT_STARTED(HttpStatus.BAD_REQUEST, "Voucher not started yet"),
    VOUCHER_EXPIRED(HttpStatus.BAD_REQUEST, "Voucher expired"),
    VOUCHER_MIN_ORDER(HttpStatus.BAD_REQUEST, "Order does not meet minimum amount for voucher"),
    VOUCHER_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "Voucher usage limit reached for this user"),

    // ===== VOUCHER (ADMIN FLOW) =====
    // ===== VOUCHER (ADMIN FLOW) =====
VOUCHER_CODE_EXISTS(HttpStatus.CONFLICT, "Voucher code already exists"),
VOUCHER_INVALID_TIME(HttpStatus.BAD_REQUEST, "Voucher start time must be before end time"),
VOUCHER_PERCENT_INVALID(HttpStatus.BAD_REQUEST, "Percent voucher value must be between 1 and 100"),
VOUCHER_ALREADY_INACTIVE(HttpStatus.BAD_REQUEST, "Voucher is already inactive"),
ACTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Action is not allowed"),
UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Update failed"),
BRANCH_ALREADY_HAVE_ADMIN(HttpStatus.BAD_REQUEST, "Branch already has admin"),
VOUCHER_ALREADY_USED(HttpStatus.BAD_REQUEST, "Voucher has already been used and cannot be deleted");
    

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
