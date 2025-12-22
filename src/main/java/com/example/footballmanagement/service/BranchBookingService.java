package com.example.footballmanagement.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.footballmanagement.dto.request.BranchBookingFilterRequest;
import com.example.footballmanagement.dto.request.UpdateBookingStatusRequest;
import com.example.footballmanagement.dto.response.BranchBookingResponse;
import com.example.footballmanagement.dto.response.UpdateBookingStatusResponse;

public interface BranchBookingService {

    /**
     * Lấy danh sách booking của chi nhánh mà admin quản lý.
     *
     * @param adminId ID của user có role ADMIN_BRANCH
     * @param filter  bộ lọc (ngày, status, tên sân, user)
     * @param pageable phân trang
     * @return trang dữ liệu các booking thuộc chi nhánh
     */
    Page<BranchBookingResponse> getBookingsOfAdminBranch(UUID adminId,
                                                         BranchBookingFilterRequest filter,
                                                         Pageable pageable);
    /**
     * Cập nhật trạng thái của booking trong chi nhánh.
     * Hỗ trợ các luồng:
     *  - APPROVED → WAITING_REFUND  (tạm ngừng hoạt động / bảo trì)
     *  - WAITING_REFUND → REFUNDED (đã hoàn tiền thành công)
     *
     * @param adminId ID của admin chi nhánh
     * @param request dữ liệu yêu cầu cập nhật
     * @return kết quả sau khi cập nhật
     */
    UpdateBookingStatusResponse updateBookingStatus(UUID adminId, UpdateBookingStatusRequest request);
}
