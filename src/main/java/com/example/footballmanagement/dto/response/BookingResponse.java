package com.example.footballmanagement.dto.response;

import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.BookingStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingResponse {
    private final UUID id;

    // ⚡ Thêm thông tin hiển thị thay vì chỉ ID
    private final String pitchName;
    private final String branchName;
    private final String userName;

    private final BookingStatus status;
    private final String note;

    // ⚡ Trả về ngày đặt sân (dựa trên slot đầu tiên)
    private final String bookingDate; 

    private final List<BookingSlotResponse> slots;
    private final VoucherUsageResponse voucherUsage;

    private final BookingPriceResponse pricing;
}
