package com.example.footballmanagement.dto.request;

import com.example.footballmanagement.entity.enums.BookingStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBookingStatusRequest {

    @NotNull(message = "bookingId is required")
    private String bookingId; // UUID của booking cần cập nhật

    @NotNull(message = "newStatus is required")
    private BookingStatus newStatus; // WAITING_REFUND hoặc REFUNDED

    private String adminNote; // ghi chú nội bộ hoặc lý do (không ghi đè note khách)
}
