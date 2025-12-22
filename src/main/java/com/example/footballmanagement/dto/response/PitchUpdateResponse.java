package com.example.footballmanagement.dto.response;

import java.util.UUID;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PitchUpdateResponse {

    private UUID id;
    private String name;
    private String location;
    private String description;

    private Boolean active;

    private Short pitchTypeId;
    private String pitchTypeName;

    private UUID branchId;
    private String branchName;
    private String branchLocation;

    // trả luôn danh sách ảnh cho FE nếu cần refresh UI
    private List<ImageDto> images;

    @Data
    @Builder
    public static class ImageDto {
        private UUID id;
        private String url;
        private boolean cover;
    }
}
