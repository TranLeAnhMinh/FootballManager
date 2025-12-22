package com.example.footballmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingOverlapResponse {
    private UUID bookingId;
    private String userName; // ✅ tên người đặt sân
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private String status; // ✅ trạng thái booking (APPROVED, PENDING, ...)
}
