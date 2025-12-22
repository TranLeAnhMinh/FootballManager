package com.example.footballmanagement.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.BranchMonthlyRevenueRequestDto;
import com.example.footballmanagement.dto.request.BranchRevenueRequestDto;
import com.example.footballmanagement.dto.response.BranchMonthlyRevenueResponseDto;
import com.example.footballmanagement.dto.response.BranchRevenueResponseDto;
import com.example.footballmanagement.entity.Branch;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.repository.BranchRepository;
import com.example.footballmanagement.service.BranchRevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchRevenueServiceImpl implements BranchRevenueService {

    private final BookingRepository bookingRepo;
    private final BranchRepository branchRepo;

    /* =====================================================
     * DAILY REVENUE
     * ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public BranchRevenueResponseDto getDailyRevenue(UUID adminId, BranchRevenueRequestDto request) {

        // 1️⃣ Xác định ngày
        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = date.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        log.info("📅 Daily revenue | admin={} | date={}", adminId, date);

        // 2️⃣ Lấy branch admin quản lý
        Branch branch = branchRepo.findByAdmin_Id(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin is not managing any branch"));
        UUID branchId = branch.getId();

        /*
         * Query trả về:
         * row[0] = totalCharged  (ALL booking đã từng thu tiền)
         * row[1] = totalRefunded (CANCELLED / REFUNDED)
         */
        List<Object[]> result = bookingRepo.calculateDailyRevenue(branchId, startOfDay, endOfDay);
        Object[] row = result.isEmpty() ? new Object[] { BigDecimal.ZERO, BigDecimal.ZERO } : result.get(0);

        BigDecimal totalCharged = row[0] != null ? (BigDecimal) row[0] : BigDecimal.ZERO;
        BigDecimal totalRefunded = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;

        BigDecimal net = totalCharged.subtract(totalRefunded);

        // 3️⃣ Tính phần trăm
        BigDecimal total = totalCharged.add(totalRefunded);
        double chargedPct = 0;
        double refundedPct = 0;

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            chargedPct = totalCharged.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            refundedPct = totalRefunded.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // 4️⃣ Build response
        return BranchRevenueResponseDto.builder()
                .approvedRevenue(totalCharged)                 // đổi nghĩa: total charged
                .cancelledOrRefundedAmount(totalRefunded)
                .netRevenue(net)
                .details(List.of(
                        BranchRevenueResponseDto.RevenueDetailItem.builder()
                                .label("Tổng tiền đã thu")
                                .amount(totalCharged)
                                .percentage(chargedPct)
                                .build(),
                        BranchRevenueResponseDto.RevenueDetailItem.builder()
                                .label("Tổng tiền đã hoàn")
                                .amount(totalRefunded)
                                .percentage(refundedPct)
                                .build()
                ))
                .build();
    }

    /* =====================================================
     * MONTHLY REVENUE
     * ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public BranchMonthlyRevenueResponseDto getMonthlyRevenue(UUID adminId, BranchMonthlyRevenueRequestDto request) {

        int year = (request != null && request.getYear() != null)
                ? request.getYear()
                : LocalDate.now().getYear();

        log.info("📊 Monthly revenue | admin={} | year={}", adminId, year);

        // 1️⃣ Lấy branch admin quản lý
        Branch branch = branchRepo.findByAdmin_Id(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin is not managing any branch"));
        UUID branchId = branch.getId();

        /*
         * Query trả về:
         * row[0] = month (1-12)
         * row[1] = totalCharged
         * row[2] = totalRefunded
         */
        List<Object[]> results = bookingRepo.calculateMonthlyRevenue(branchId, year);

        BranchMonthlyRevenueResponseDto.MonthlyRevenueItem[] months =
                new BranchMonthlyRevenueResponseDto.MonthlyRevenueItem[12];

        // Init 12 months = 0
        for (int i = 0; i < 12; i++) {
            months[i] = BranchMonthlyRevenueResponseDto.MonthlyRevenueItem.builder()
                    .month(i + 1)
                    .approvedRevenue(BigDecimal.ZERO)
                    .cancelledOrRefunded(BigDecimal.ZERO)
                    .netRevenue(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal totalYearRevenue = BigDecimal.ZERO;

        // Fill data
        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            BigDecimal charged = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            BigDecimal refunded = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
            BigDecimal net = charged.subtract(refunded);

            months[month - 1] = BranchMonthlyRevenueResponseDto.MonthlyRevenueItem.builder()
                    .month(month)
                    .approvedRevenue(charged)
                    .cancelledOrRefunded(refunded)
                    .netRevenue(net)
                    .build();

            totalYearRevenue = totalYearRevenue.add(net);
        }

        return BranchMonthlyRevenueResponseDto.builder()
                .year(year)
                .totalNetRevenue(totalYearRevenue)
                .monthlyRevenues(List.of(months))
                .build();
    }
}
