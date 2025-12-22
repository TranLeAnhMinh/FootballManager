package com.example.footballmanagement.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.dto.request.BookingSlotRequest;
import com.example.footballmanagement.dto.response.BookingPriceResponse;

public interface PricingService {

    /**
     * Tính giá gốc, áp voucher và trả về tổng kết giá
     */
    BookingPriceResponse calculatePrice(
            UUID pitchId,
            List<BookingSlotRequest> slots,
            String voucherCode,
            UUID userId
    );

    /**
     * Tính giá gốc (chưa voucher)
     */
    BigDecimal calculateBasePrice(UUID pitchId, List<BookingSlotRequest> slots);
}
