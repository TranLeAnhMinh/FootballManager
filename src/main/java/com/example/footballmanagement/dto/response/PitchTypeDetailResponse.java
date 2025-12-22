package com.example.footballmanagement.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchTypeDetailResponse {

    private Short id;
    private String name;
    private List<PitchSummaryResponse> pitches;
}
