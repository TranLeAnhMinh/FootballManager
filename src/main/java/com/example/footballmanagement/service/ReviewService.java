package com.example.footballmanagement.service;

import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.dto.request.ReviewRequest;
import com.example.footballmanagement.dto.response.ReviewResponse;

public interface ReviewService {

    // Lấy tất cả review của 1 pitch
    List<ReviewResponse> getReviewsByPitch(UUID pitchId);

    // Tạo mới hoặc cập nhật review cho 1 pitch
    ReviewResponse addOrUpdateReview(UUID pitchId, ReviewRequest request);

    // Lấy trung bình rating (đã làm tròn về 0.5)
    double getAverageRating(UUID pitchId);
}
