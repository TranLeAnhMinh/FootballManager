package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class BranchBookingResponse {
    private UUID bookingId;

    // Thông tin user đặt sân
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;

    // Thông tin sân
    private UUID pitchId;
    private String pitchName;
    private String pitchType;     // 5-a-side, 7-a-side, ...
    private String pitchLocation;

    // Thông tin slot (thời gian đá)
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    // Trạng thái và giá
    private String status;
    private BigDecimal finalPrice;

    // Ghi chú
    private String note;

    // Thời điểm tạo
    private OffsetDateTime createdAt;
}
