package com.example.footballmanagement.dto.pricing;

import java.util.List;

import lombok.Data;

@Data
public class PriceRuleCondition {
    private List<Integer> daysOfWeek;  // ví dụ [6,7] = T7, CN
    private Integer startHour;         // ví dụ 18
    private Integer endHour;           // ví dụ 22
}
