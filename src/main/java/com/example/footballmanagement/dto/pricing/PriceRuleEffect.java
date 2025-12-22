package com.example.footballmanagement.dto.pricing;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PriceRuleEffect {
    private String type;       // "percent" | "fixed"
    private BigDecimal value;  // 20 (percent) hoặc 50000 (fixed)
}
