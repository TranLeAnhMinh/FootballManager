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
public class PitchSummaryResponse {

    private UUID id;
    private String name;
    private String location;
    private String description;
    private boolean active;

    // Danh sách URL ảnh (cover + ảnh phụ)
    private String coverImageUrl;

    // Điểm đánh giá trung bình (tính từ các review)
    private Double averageRating;
}
