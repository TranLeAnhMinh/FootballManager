package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.constant.Endpoint;
import com.example.footballmanagement.dto.request.BookingRequest;
import com.example.footballmanagement.dto.response.BookingHistoryResponse;
import com.example.footballmanagement.dto.response.BookingResponse;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Endpoint.BOOKING_API_BASE) // ✅ dùng constant thay vì hardcode
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepo;

    /**
     * ✅ Tạo booking (chỉ user login được)
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getId();
        BookingResponse response = bookingService.createBooking(request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Lấy lịch sử booking của user
     */
    @GetMapping("/history")
    public ResponseEntity<Page<BookingHistoryResponse>> getMyBookingHistory(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = userDetails.getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(bookingService.getBookingHistory(userId, pageable));
    }

    /**
     * ✅ Xem chi tiết booking
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingDetail(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // ⚡ check quyền
        if (!booking.getUser().getId().equals(userDetails.getId())) {
            throw new SecurityException("You are not allowed to view this booking");
        }

        return ResponseEntity.ok(bookingService.getBookingResponseById(bookingId));
    }
}
