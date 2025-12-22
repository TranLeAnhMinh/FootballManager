package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoucherUsageResponse {
    private final UUID voucherId;
    private final String code;
    private final String type;          // PERCENT | FIXED
    private final BigDecimal discountAmount;
    private final OffsetDateTime usedAt;
}
