package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateBasePriceCellResponse {

    private UUID basePriceId;
    private UUID pitchId;
    private Short dayOfWeek;

    private LocalTime timeStart;
    private LocalTime timeEnd;

    private BigDecimal price;
    private String message;
}