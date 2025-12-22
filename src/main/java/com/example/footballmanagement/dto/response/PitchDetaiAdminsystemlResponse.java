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
public class PitchDetaiAdminsystemlResponse {
     private UUID id;

    private UUID branchId;
    private String branchName;
    private String branchLocation;

    private Short pitchTypeId;
    private String pitchTypeName;

    private String name;
    private String location;
    private String description;

    private boolean active;

    // Danh sách ảnh
    private List<ImageDto> images;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {
        private UUID id;
        private String url;
        private boolean cover; // ảnh đại diện
    }
}
