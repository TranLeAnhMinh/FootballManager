package com.example.footballmanagement.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.dto.request.ReviewRequest;
import com.example.footballmanagement.dto.response.ReviewResponse;
import com.example.footballmanagement.service.ReviewService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/pitches/{pitchId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Lấy danh sách review của 1 pitch
    @GetMapping
    public List<ReviewResponse> getReviews(@PathVariable UUID pitchId) {
        return reviewService.getReviewsByPitch(pitchId);
    }

    // Thêm mới hoặc update review (mỗi user chỉ 1 review)
    @PostMapping
    public ReviewResponse addOrUpdateReview(
            @PathVariable UUID pitchId,
            @RequestBody ReviewRequest request
    ) {
        return reviewService.addOrUpdateReview(pitchId, request);
    }

    // Lấy điểm trung bình (đã làm tròn về .0 hoặc .5)
    @GetMapping("/average")
    public double getAverageRating(@PathVariable UUID pitchId) {
        return reviewService.getAverageRating(pitchId);
    }
}