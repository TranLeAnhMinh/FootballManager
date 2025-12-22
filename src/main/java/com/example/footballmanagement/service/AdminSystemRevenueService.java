package com.example.footballmanagement.service;

import java.time.LocalDate;
import java.util.UUID;

import com.example.footballmanagement.dto.response.BranchMonthlyRevenueResponseDto;
import com.example.footballmanagement.dto.response.BranchRevenueResponseDto;

/**
 * ADMIN SYSTEM
 * Xem doanh thu toàn hệ thống hoặc 1 branch cụ thể
 */
public interface AdminSystemRevenueService {

    /**
     * Doanh thu TOÀN HỆ THỐNG trong 1 ngày
     */
    BranchRevenueResponseDto getSystemDailyRevenue(LocalDate date);

    /**
     * Doanh thu TOÀN HỆ THỐNG theo từng tháng trong 1 năm
     */
    BranchMonthlyRevenueResponseDto getSystemMonthlyRevenue(int year);

    /**
     * Doanh thu 1 BRANCH CỤ THỂ trong 1 ngày
     */
    BranchRevenueResponseDto getBranchDailyRevenue(UUID branchId, LocalDate date);

    /**
     * Doanh thu 1 BRANCH CỤ THỂ theo từng tháng trong 1 năm
     */
    BranchMonthlyRevenueResponseDto getBranchMonthlyRevenue(UUID branchId, int year);
}
