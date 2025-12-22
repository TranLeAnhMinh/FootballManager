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

import com.example.footballmanagement.dto.response.BranchMonthlyRevenueResponseDto;
import com.example.footballmanagement.dto.response.BranchRevenueResponseDto;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.service.AdminSystemRevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSystemRevenueServiceImpl implements AdminSystemRevenueService {

    private final BookingRepository bookingRepo;

    /* =====================================================
     * SYSTEM DAILY REVENUE
     * ===================================================== */
@Override
@Transactional(readOnly = true)
public BranchRevenueResponseDto getSystemDailyRevenue(LocalDate date) {

    LocalDate targetDate = (date != null) ? date : LocalDate.now();
    OffsetDateTime startOfDay = targetDate.atStartOfDay().atOffset(ZoneOffset.UTC);
    OffsetDateTime endOfDay = targetDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

    log.info("📅 System daily revenue | date={}", targetDate);

    Object rawResult = bookingRepo.calculateSystemDailyRevenue(startOfDay, endOfDay);

    BigDecimal charged = BigDecimal.ZERO;
    BigDecimal refunded = BigDecimal.ZERO;

    if (rawResult != null) {
        Object[] row = (Object[]) rawResult;      // unwrap tầng 1
        Object[] values = (Object[]) row[0];      // unwrap tầng 2

        if (values[0] != null) charged = (BigDecimal) values[0];
        if (values[1] != null) refunded = (BigDecimal) values[1];
    }

    BigDecimal net = charged.subtract(refunded);

    return buildDailyResponse(charged, refunded, net);
}



    /* =====================================================
     * SYSTEM MONTHLY REVENUE
     * ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public BranchMonthlyRevenueResponseDto getSystemMonthlyRevenue(int year) {

        int targetYear = year > 0 ? year : LocalDate.now().getYear();
        log.info("📊 System monthly revenue | year={}", targetYear);

        List<Object[]> results = bookingRepo.calculateSystemMonthlyRevenue(targetYear);
        return buildMonthlyResponse(results, targetYear);
    }

    /* =====================================================
     * BRANCH DAILY REVENUE (ADMIN SYSTEM)
     * ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public BranchRevenueResponseDto getBranchDailyRevenue(UUID branchId, LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        OffsetDateTime start = targetDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = targetDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        log.info("📅 Branch daily revenue | branch={} | date={}", branchId, targetDate);

        List<Object[]> result = bookingRepo.calculateDailyRevenue(branchId, start, end);
        Object[] row = result.isEmpty()
                ? new Object[] { BigDecimal.ZERO, BigDecimal.ZERO }
                : result.get(0);

        BigDecimal charged = row[0] != null ? (BigDecimal) row[0] : BigDecimal.ZERO;
        BigDecimal refunded = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
        BigDecimal net = charged.subtract(refunded);

        return buildDailyResponse(charged, refunded, net);
    }

    /* =====================================================
     * BRANCH MONTHLY REVENUE (ADMIN SYSTEM)
     * ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public BranchMonthlyRevenueResponseDto getBranchMonthlyRevenue(UUID branchId, int year) {

        int targetYear = year > 0 ? year : LocalDate.now().getYear();
        log.info("📊 Branch monthly revenue | branch={} | year={}", branchId, targetYear);

        List<Object[]> results = bookingRepo.calculateMonthlyRevenue(branchId, targetYear);
        return buildMonthlyResponse(results, targetYear);
    }

    /* =====================================================
     * COMMON BUILDERS
     * ===================================================== */
    private BranchRevenueResponseDto buildDailyResponse(
            BigDecimal charged, BigDecimal refunded, BigDecimal net) {

        BigDecimal total = charged.add(refunded);
        double chargedPct = 0;
        double refundedPct = 0;

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            chargedPct = charged.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            refundedPct = refunded.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return BranchRevenueResponseDto.builder()
                .approvedRevenue(charged)
                .cancelledOrRefundedAmount(refunded)
                .netRevenue(net)
                .details(List.of(
                        BranchRevenueResponseDto.RevenueDetailItem.builder()
                                .label("Tổng tiền đã thu")
                                .amount(charged)
                                .percentage(chargedPct)
                                .build(),
                        BranchRevenueResponseDto.RevenueDetailItem.builder()
                                .label("Tổng tiền đã hoàn")
                                .amount(refunded)
                                .percentage(refundedPct)
                                .build()
                ))
                .build();
    }

    private BranchMonthlyRevenueResponseDto buildMonthlyResponse(
            List<Object[]> results, int year) {

        BranchMonthlyRevenueResponseDto.MonthlyRevenueItem[] months =
                new BranchMonthlyRevenueResponseDto.MonthlyRevenueItem[12];

        for (int i = 0; i < 12; i++) {
            months[i] = BranchMonthlyRevenueResponseDto.MonthlyRevenueItem.builder()
                    .month(i + 1)
                    .approvedRevenue(BigDecimal.ZERO)
                    .cancelledOrRefunded(BigDecimal.ZERO)
                    .netRevenue(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal totalYearRevenue = BigDecimal.ZERO;

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
