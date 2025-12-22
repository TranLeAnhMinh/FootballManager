package com.example.footballmanagement.service;

import java.util.UUID;

import com.example.footballmanagement.dto.request.MaintenanceWindowDeleteRequest;
import com.example.footballmanagement.dto.response.MaintenanceWindowDeleteResponse;

public interface MaintenanceWindowDeleteService {

    /**
     * Xóa maintenance window (chỉ admin branch của chi nhánh có sân đó mới được xóa).
     * Chỉ cho phép xóa nếu startAt > now().
     */
    MaintenanceWindowDeleteResponse deleteMaintenanceWindow(UUID adminId, MaintenanceWindowDeleteRequest req);
}
