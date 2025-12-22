package com.example.footballmanagement.dto.request;

import java.time.OffsetDateTime;

import com.example.footballmanagement.entity.enums.BookingStatus;

import lombok.Data;

@Data
public class BranchBookingFilterRequest {
    private OffsetDateTime startDate; // lọc từ ngày nào (optional)
    private OffsetDateTime endDate;   // đến ngày nào (optional)
    private BookingStatus status;            // trạng thái booking (PENDING, APPROVED, ...)
    private String pitchName;         // tên sân chứa chuỗi này (optional)
    private String userKeyword;       // lọc user theo tên/email (optional)
}
