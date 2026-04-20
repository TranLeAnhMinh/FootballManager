package com.example.footballmanagement.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyBasePriceTemplateRequest {

    private List<UUID> pitchIds;
    private List<Short> dayOfWeeks;

    private LocalTime startTime;
    private LocalTime endTime;

    private BigDecimal price;
}