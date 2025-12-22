package com.example.footballmanagement.dto.request;

import java.time.OffsetDateTime;

import com.example.footballmanagement.entity.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho API GET /api/branch-admin/bookings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListRequest {

    /** ID sân (nếu lọc theo sân cụ thể) */
    private String pitchId; // UUID dạng String để truyền qua query param

    /** ID loại sân (5,7,11) */
    private Short pitchTypeId;

    /** Trạng thái booking */
    private BookingStatus status;

    /** Từ ngày (lọc theo createdAt hoặc slot.startAt) */
    private OffsetDateTime fromDate;

    /** Đến ngày */
    private OffsetDateTime toDate;

    /** Từ khóa tìm kiếm (tên khách / mã đơn) */
    private String keyword;

    /** Trang hiện tại (0-based) */
    @Builder.Default
    private int page = 0;

    /** Kích thước trang */
    @Builder.Default
    private int size = 10;
}
