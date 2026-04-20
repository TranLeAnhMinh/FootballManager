package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasePriceGridCellResponse {

    private UUID basePriceId;     // null nếu ô này chưa có cấu hình
    private Short dayOfWeek;      // 1 -> 7
    private BigDecimal price;     // null nếu chưa cấu hình
    private boolean configured;   // true nếu đã có giá, false nếu ô trống
}