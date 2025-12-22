package com.example.footballmanagement.dto.response;

import java.util.List;
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
public class PitchTypeBranchesResponse {
    private Short pitchTypeId;
    private String pitchTypeName;

    private List<BranchSummaryDTO> branches;

    // ===== Inner static class =====
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchSummaryDTO {
        private UUID id;
        private String name;
        private List<PitchSummaryDTO> pitches;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PitchSummaryDTO {
        private UUID id;
        private String name;
        private boolean active;
    }
}
