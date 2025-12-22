package com.example.footballmanagement.dto.response;

import com.example.footballmanagement.entity.enums.BookingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBookingStatusResponse {
    private String bookingId;
    private BookingStatus oldStatus;
    private BookingStatus newStatus;
    private String adminNote;
    private String message; // ví dụ: "Booking status updated and email sent successfully"
}
