package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.request.BranchMonthlyRevenueRequestDto;
import com.example.footballmanagement.dto.request.BranchRevenueRequestDto;
import com.example.footballmanagement.dto.response.BranchMonthlyRevenueResponseDto;
import com.example.footballmanagement.dto.response.BranchRevenueResponseDto;
import com.example.footballmanagement.service.BranchRevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/revenue/branch")
@RequiredArgsConstructor
public class BranchRevenueController {

    private final BranchRevenueService branchRevenueService;

    /**
     * ✅ API dành cho ADMIN_BRANCH:
     * Lấy doanh thu của chi nhánh mà admin đang quản lý (theo ngày).
     *
     * Request body (optional):
     * {
     *   "date": "2025-10-26"
     * }
     *
     * Nếu không gửi date → mặc định là ngày hôm nay.
     */
    @PostMapping("/daily")
    public ResponseEntity<BranchRevenueResponseDto> getDailyRevenue(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody(required = false) BranchRevenueRequestDto request
    ) {
        UUID adminId = userDetails.getId();

        log.info("📊 [API] Admin {} yêu cầu xem doanh thu chi nhánh theo ngày", adminId);

        BranchRevenueResponseDto response = branchRevenueService.getDailyRevenue(adminId, request);

        return ResponseEntity.ok(response);
    }

      /**
     * ✅ API dành cho ADMIN_BRANCH:
     * Lấy doanh thu theo tháng của chi nhánh (12 tháng trong năm).
     *
     * Request (optional):
     * {
     *   "year": 2025
     * }
     * → Nếu không gửi year → mặc định là năm hiện tại.
     *
     * Response:
     * {
     *   "year": 2025,
     *   "totalNetRevenue": 1234567.89,
     *   "monthlyRevenues": [
     *      { "month": 1, "approvedRevenue": 0, "cancelledOrRefunded": 0, "netRevenue": 0 },
     *      { "month": 2, "approvedRevenue": 500000, "cancelledOrRefunded": 10000, "netRevenue": 490000 },
     *      ...
     *   ]
     * }
     */
    @PostMapping("/monthly")
    public ResponseEntity<BranchMonthlyRevenueResponseDto> getMonthlyRevenue(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody(required = false) BranchMonthlyRevenueRequestDto request
    ) {
        UUID adminId = userDetails.getId();
        log.info("📊 [API] Admin {} yêu cầu xem doanh thu chi nhánh theo tháng", adminId);

        BranchMonthlyRevenueResponseDto response = branchRevenueService.getMonthlyRevenue(adminId, request);
        return ResponseEntity.ok(response);
    }
}
