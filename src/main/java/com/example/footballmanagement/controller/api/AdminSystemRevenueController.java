package com.example.footballmanagement.controller.api;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.request.BranchMonthlyRevenueRequestDto;
import com.example.footballmanagement.dto.request.BranchRevenueRequestDto;
import com.example.footballmanagement.dto.response.BranchMonthlyRevenueResponseDto;
import com.example.footballmanagement.dto.response.BranchRevenueResponseDto;
import com.example.footballmanagement.service.AdminSystemRevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/adminsystem/revenue")
@RequiredArgsConstructor
public class AdminSystemRevenueController {

    private final AdminSystemRevenueService adminSystemRevenueService;

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Lấy doanh thu TOÀN HỆ THỐNG (theo ngày).
     *
     * Request body (optional):
     * { "date": "2025-10-26" }
     *
     * Nếu không gửi date → mặc định là hôm nay.
     */
    @PostMapping("/system/daily")
    public ResponseEntity<BranchRevenueResponseDto> getSystemDailyRevenue(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody(required = false) BranchRevenueRequestDto request
    ) {
        UUID adminSystemId = userDetails.getId();
        LocalDate date = (request != null) ? request.getDate() : null;

        log.info("📊 [ADMIN SYSTEM API] AdminSystem {} yêu cầu xem doanh thu toàn hệ thống theo ngày {}", adminSystemId, date);

        BranchRevenueResponseDto response = adminSystemRevenueService.getSystemDailyRevenue(date);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Lấy doanh thu TOÀN HỆ THỐNG theo tháng (12 tháng trong năm).
     *
     * Request body (optional):
     * { "year": 2025 }
     *
     * Nếu không gửi year → mặc định là năm hiện tại.
     */
    @PostMapping("/system/monthly")
    public ResponseEntity<BranchMonthlyRevenueResponseDto> getSystemMonthlyRevenue(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody(required = false) BranchMonthlyRevenueRequestDto request
    ) {
        UUID adminSystemId = userDetails.getId();
        Integer year = (request != null) ? request.getYear() : null;

        log.info("📊 [ADMIN SYSTEM API] AdminSystem {} yêu cầu xem doanh thu toàn hệ thống theo năm {}", adminSystemId, year);

        BranchMonthlyRevenueResponseDto response =
                adminSystemRevenueService.getSystemMonthlyRevenue(year != null ? year : 0);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Lấy doanh thu 1 BRANCH cụ thể (theo ngày).
     *
     * Request body (optional):
     * { "date": "2025-10-26" }
     */
    @PostMapping("/branch/{branchId}/daily")
    public ResponseEntity<BranchRevenueResponseDto> getBranchDailyRevenue(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable UUID branchId,
            @Validated @RequestBody(required = false) BranchRevenueRequestDto request
    ) {
        UUID adminSystemId = userDetails.getId();
        LocalDate date = (request != null) ? request.getDate() : null;

        log.info("📊 [ADMIN SYSTEM API] AdminSystem {} yêu cầu xem doanh thu branch {} theo ngày {}", adminSystemId, branchId, date);

        BranchRevenueResponseDto response = adminSystemRevenueService.getBranchDailyRevenue(branchId, date);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Lấy doanh thu 1 BRANCH cụ thể theo tháng (12 tháng trong năm).
     *
     * Request body (optional):
     * { "year": 2025 }
     */
    @PostMapping("/branch/{branchId}/monthly")
    public ResponseEntity<BranchMonthlyRevenueResponseDto> getBranchMonthlyRevenue(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable UUID branchId,
            @Validated @RequestBody(required = false) BranchMonthlyRevenueRequestDto request
    ) {
        UUID adminSystemId = userDetails.getId();
        Integer year = (request != null) ? request.getYear() : null;

        log.info("📊 [ADMIN SYSTEM API] AdminSystem {} yêu cầu xem doanh thu branch {} theo năm {}", adminSystemId, branchId, year);

        BranchMonthlyRevenueResponseDto response =
                adminSystemRevenueService.getBranchMonthlyRevenue(branchId, year != null ? year : 0);

        return ResponseEntity.ok(response);
    }
}
