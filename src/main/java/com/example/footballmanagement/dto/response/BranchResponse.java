package com.example.footballmanagement.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {

    private UUID id;
    private String name;
    private String location;
    private String description;

    // Danh sách sân thuộc chi nhánh (có thể rỗng)
    private List<PitchSummary> pitches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PitchSummary {
        private UUID id;
        private String name;
        private String location;
        private String description;
        private boolean active;
        private Short pitchTypeId;
        private String pitchTypeName;
    }
}
