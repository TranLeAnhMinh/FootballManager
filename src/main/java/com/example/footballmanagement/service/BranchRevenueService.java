package com.example.footballmanagement.service;

import java.util.UUID;

import com.example.footballmanagement.dto.request.BranchMonthlyRevenueRequestDto;
import com.example.footballmanagement.dto.request.BranchRevenueRequestDto;
import com.example.footballmanagement.dto.response.BranchMonthlyRevenueResponseDto;
import com.example.footballmanagement.dto.response.BranchRevenueResponseDto;

public interface BranchRevenueService {

    /**
     * Lấy doanh thu của chi nhánh mà admin quản lý trong một ngày cụ thể.
     *
     * @param adminId ID của user có role ADMIN_BRANCH
     * @param request chứa ngày cần xem (nếu null thì mặc định là hôm nay)
     * @return dữ liệu doanh thu gồm tổng thu, bị hủy, và netRevenue
     */
    BranchRevenueResponseDto getDailyRevenue(UUID adminId, BranchRevenueRequestDto request);

     /**
     * Lấy doanh thu của chi nhánh mà admin quản lý theo từng tháng trong một năm.
     * @param adminId ID của admin chi nhánh
     * @param request chứa năm cần xem (nếu null thì mặc định là năm hiện tại)
     * @return dữ liệu gồm 12 tháng và tổng doanh thu cả năm
     */
    BranchMonthlyRevenueResponseDto getMonthlyRevenue(UUID adminId, BranchMonthlyRevenueRequestDto request);
}
