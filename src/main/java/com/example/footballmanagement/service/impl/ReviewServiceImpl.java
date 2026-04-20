package com.example.footballmanagement.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.ReviewRequest;
import com.example.footballmanagement.dto.response.ReviewResponse;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.entity.Review;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.BookingStatus;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.repository.PitchRepository;
import com.example.footballmanagement.repository.ReviewRepository;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.ReviewService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PitchRepository pitchRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ReviewResponse> getReviewsByPitch(UUID pitchId) {
        return reviewRepository.findByPitch_Id(pitchId).stream()
                .map(ConverterUtil::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse addOrUpdateReview(UUID pitchId, UUID userId, ReviewRequest request) {
        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        boolean hasBooking = bookingRepository.existsByUser_IdAndPitch_IdAndStatusIn(
                user.getId(),
                pitch.getId(),
                List.of(BookingStatus.APPROVED, BookingStatus.CHECKED_IN)
        );

        if (!hasBooking) {
            throw new IllegalStateException("Bạn chưa từng đặt sân này nên không thể đánh giá.");
        }

        Review review = reviewRepository.findByPitch_IdAndUser_Id(pitchId, user.getId())
                .orElse(null);

        if (review == null) {
            review = ConverterUtil.fromReviewRequest(pitch, user, request);
        } else {
            review.setRating(request.getRating());
            review.setContent(request.getContent());
        }

        Review saved = reviewRepository.save(review);
        return ConverterUtil.toReviewResponse(saved);
    }

    @Override
    public double getAverageRating(UUID pitchId) {
        List<Review> reviews = reviewRepository.findByPitch_Id(pitchId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return roundToHalf(avg);
    }

    @Override
    public Map<UUID, Double> getAverageRatingMap(List<UUID> pitchIds) {
        if (pitchIds == null || pitchIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> rows = reviewRepository.findAverageRatingByPitchIds(pitchIds);
        Map<UUID, Double> result = new HashMap<>();

        for (Object[] row : rows) {
            UUID pitchId = (UUID) row[0];
            Double avg = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
            result.put(pitchId, roundToHalf(avg));
        }

        return result;
    }

    private double roundToHalf(double value) {
        return Math.round(value * 2.0) / 2.0;
    }
}