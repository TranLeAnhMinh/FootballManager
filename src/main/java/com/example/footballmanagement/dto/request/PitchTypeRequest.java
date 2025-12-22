package com.example.footballmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PitchTypeRequest {
    private Short pitchTypeId; // 5, 7, 11
}
