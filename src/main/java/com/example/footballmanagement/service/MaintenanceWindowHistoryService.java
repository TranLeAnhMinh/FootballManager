package com.example.footballmanagement.service;

import java.util.UUID;

import com.example.footballmanagement.dto.paginated.PaginatedResponse;
import com.example.footballmanagement.dto.request.MaintenanceWindowFilterRequest;
import com.example.footballmanagement.dto.response.MaintenanceWindowFilterResponse;

public interface MaintenanceWindowHistoryService {

    /**
     * Lấy danh sách lịch sử bảo trì (maintenance window) của tất cả sân
     * thuộc chi nhánh mà admin đang quản lý.
     */
    PaginatedResponse<MaintenanceWindowFilterResponse> getBranchMaintenanceHistory(
            UUID adminId,
            MaintenanceWindowFilterRequest req
    );
}
