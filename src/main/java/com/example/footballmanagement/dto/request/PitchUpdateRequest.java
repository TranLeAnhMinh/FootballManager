package com.example.footballmanagement.dto.request;

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
public class PitchUpdateRequest {

    private String name;

    private String location;

    private String description;

    private Boolean active;

    private Short pitchTypeId;
}
