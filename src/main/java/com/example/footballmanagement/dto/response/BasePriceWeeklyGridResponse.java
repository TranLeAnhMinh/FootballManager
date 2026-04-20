package com.example.footballmanagement.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasePriceWeeklyGridResponse {

    private UUID pitchId;
    private String pitchName;

    // 32 dòng time slot, mỗi dòng chứa giá của 7 ngày
    private List<BasePriceGridRowResponse> rows;
}