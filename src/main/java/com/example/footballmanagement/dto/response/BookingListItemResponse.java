package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO - thông tin 1 đơn đặt sân trong danh sách lịch sử.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListItemResponse {
     /** Mã đơn */
    private UUID id;

    /** Tên khách hàng */
    private String customerName;

    /** Liên hệ (email hoặc phone) */
    private String customerContact;

    /** Tên sân */
    private String pitchName;

    /** Loại sân (ví dụ "5-a-side") */
    private String pitchTypeName;

    /** Giờ bắt đầu sớm nhất trong các slot */
    private OffsetDateTime startAt;

    /** Giờ kết thúc muộn nhất trong các slot */
    private OffsetDateTime endAt;

    /** Tổng tiền */
    private BigDecimal finalPrice;

    /** Trạng thái đơn */
    private BookingStatus status;

    /** Ngày tạo đơn */
    private OffsetDateTime createdAt;
}
