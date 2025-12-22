package com.example.footballmanagement.dto.response;

import java.util.UUID;

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
public class PitchDetailResponse {
    private UUID id;
    private String name;
    private String location;
    private String description;
    private boolean active;
    private UUID branchId;
    private String branchName;
    private Short pitchTypeId;
    private String pitchTypeName;
}
