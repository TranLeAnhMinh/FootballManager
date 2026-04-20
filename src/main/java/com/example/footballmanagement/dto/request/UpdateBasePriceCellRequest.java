package com.example.footballmanagement.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBasePriceCellRequest {

    private UUID pitchId;
    private Short dayOfWeek;      // 1 -> 7

    private LocalTime timeStart;
    private LocalTime timeEnd;

    private BigDecimal price;
}