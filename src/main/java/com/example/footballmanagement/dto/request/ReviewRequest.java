package com.example.footballmanagement.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @NotNull(message = "pitchId không được để trống")
    private UUID pitchId;

    @NotNull(message = "userId không được để trống")
    private UUID userId;

    @Min(value = 1, message = "Rating tối thiểu là 1")
    @Max(value = 5, message = "Rating tối đa là 5")
    private short rating;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    private String content;
}
