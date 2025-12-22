package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoucherPreviewResponse {
    private final String code;
    private final String type;             // PERCENT | FIXED
    private final BigDecimal value;        // Giá trị (10% hoặc 50,000)
    private final BigDecimal maxDiscount;  // Nếu có
    private final BigDecimal discountApplied; // Số tiền giảm thực tế
}
