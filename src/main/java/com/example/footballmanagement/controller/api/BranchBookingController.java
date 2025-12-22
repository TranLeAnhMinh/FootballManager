package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.request.BranchBookingFilterRequest;
import com.example.footballmanagement.dto.request.UpdateBookingStatusRequest;
import com.example.footballmanagement.dto.response.BranchBookingResponse;
import com.example.footballmanagement.dto.response.UpdateBookingStatusResponse;
import com.example.footballmanagement.entity.enums.BookingStatus;
import com.example.footballmanagement.service.BranchBookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings/branch")
@RequiredArgsConstructor
public class BranchBookingController {

    private final BranchBookingService branchBookingService;

    /**
     * ✅ API dành cho ADMIN_BRANCH:
     * Lấy danh sách booking của chi nhánh mà admin đang quản lý.
     */
    @GetMapping
    public ResponseEntity<Page<BranchBookingResponse>> getBranchBookings(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String pitchName,
            @RequestParam(required = false) String userKeyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        // 1️⃣ Xác định admin hiện tại
        UUID adminId = userDetails.getId();

        // 2️⃣ Map request params → DTO filter
        BranchBookingFilterRequest filter = new BranchBookingFilterRequest();

        // ✅ Parse status String → Enum BookingStatus (APPROVED, PENDING, ...)
        if (status != null && !status.isBlank()) {
            try {
                filter.setStatus(BookingStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid booking status: " + status);
            }
        }

        filter.setPitchName(pitchName);
        filter.setUserKeyword(userKeyword);

        // ✅ parse ngày nếu có
        if (startDate != null && !startDate.isBlank()) {
            filter.setStartDate(java.time.OffsetDateTime.parse(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            filter.setEndDate(java.time.OffsetDateTime.parse(endDate));
        }

        // 3️⃣ Tạo pageable (sort theo createdAt DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 4️⃣ Gọi service
        Page<BranchBookingResponse> bookings =
                branchBookingService.getBookingsOfAdminBranch(adminId, filter, pageable);

        // 5️⃣ Trả về kết quả
        return ResponseEntity.ok(bookings);
    }

    // ✅ API: Cập nhật trạng thái booking (APPROVED → WAITING_REFUND → REFUNDED)
    // ============================================================
    @PostMapping("/update-status")
    public ResponseEntity<UpdateBookingStatusResponse> updateBookingStatus(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody UpdateBookingStatusRequest request
    ) {
        UUID adminId = userDetails.getId();
        UpdateBookingStatusResponse response = branchBookingService.updateBookingStatus(adminId, request);
        return ResponseEntity.ok(response);
    }
}
