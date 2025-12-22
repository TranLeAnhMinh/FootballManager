package com.example.footballmanagement.dto.request;

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
public class PitchCreateRequest {

    private String name;           // Tên sân
    private String location;       // Vị trí cụ thể trong chi nhánh
    private String description;    // Mô tả sân

    private UUID branchId;         // 🔥 Bắt buộc — sân phải thuộc 1 chi nhánh
    private Short pitchTypeId;     // 🔥 Bắt buộc — loại sân (5, 7, 11 người)

    private List<PitchImageRequest> images; // 🔹 Danh sách ảnh của sân
}
