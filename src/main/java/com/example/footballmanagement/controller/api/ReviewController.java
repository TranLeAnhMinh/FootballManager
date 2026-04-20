package com.example.footballmanagement.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.request.ReviewRequest;
import com.example.footballmanagement.dto.response.ReviewResponse;
import com.example.footballmanagement.service.ReviewService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/pitches/{pitchId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ✅ Lấy danh sách review của 1 pitch
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @PathVariable UUID pitchId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByPitch(pitchId));
    }

    // ✅ Thêm hoặc update review (phải login)
    @PostMapping
    public ResponseEntity<ReviewResponse> addOrUpdateReview(
            @PathVariable UUID pitchId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getId(); // 🔥 lấy từ JWT

        ReviewResponse response = reviewService.addOrUpdateReview(pitchId, userId, request);
        return ResponseEntity.ok(response);
    }

    // ✅ Lấy rating trung bình
    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable UUID pitchId
    ) {
        return ResponseEntity.ok(reviewService.getAverageRating(pitchId));
    }
}