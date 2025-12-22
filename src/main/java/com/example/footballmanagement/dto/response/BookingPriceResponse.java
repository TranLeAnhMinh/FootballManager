package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingPriceResponse {
    private final BigDecimal basePrice;       // giá gốc
    private final BigDecimal voucherDiscount; // số tiền giảm
    private final BigDecimal finalPrice;      // giá sau khi giảm
    private final String currency;            // ví dụ: "VND"
}
