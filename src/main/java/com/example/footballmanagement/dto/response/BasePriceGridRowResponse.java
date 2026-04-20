package com.example.footballmanagement.dto.response;

import java.time.LocalTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasePriceGridRowResponse {

    private LocalTime timeStart;
    private LocalTime timeEnd;

    // 7 cell tương ứng thứ 2 -> CN
    private List<BasePriceGridCellResponse> cells;
}