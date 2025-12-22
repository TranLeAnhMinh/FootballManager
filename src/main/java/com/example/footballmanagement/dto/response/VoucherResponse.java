package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.VoucherType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherResponse {
    private UUID id;
    private String code;
    private VoucherType type; // PERCENT | FIXED
    private BigDecimal value;
    private BigDecimal maxDiscount;
    private BigDecimal minOrder;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private boolean active;
}
