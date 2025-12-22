package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.paginated.PaginatedResponse;
import com.example.footballmanagement.dto.request.MaintenanceWindowDeleteRequest;
import com.example.footballmanagement.dto.request.MaintenanceWindowFilterRequest;
import com.example.footballmanagement.dto.response.MaintenanceWindowDeleteResponse;
import com.example.footballmanagement.dto.response.MaintenanceWindowFilterResponse;
import com.example.footballmanagement.service.MaintenanceWindowDeleteService;
import com.example.footballmanagement.service.MaintenanceWindowHistoryService;

import lombok.RequiredArgsConstructor;

/**
 * ✅ Controller dành riêng cho ADMIN_BRANCH
 * - Xem lịch sử bảo trì của chi nhánh
 * - Xóa maintenance window (nếu chưa bắt đầu)
 */
@RestController
@RequestMapping("/api/admin/maintenance-windows")
@RequiredArgsConstructor
public class MaintenanceWindowAdminController {

    private final MaintenanceWindowHistoryService maintenanceWindowHistoryService;
    private final MaintenanceWindowDeleteService maintenanceWindowDeleteService;

    /**
     * 📋 Lấy danh sách lịch sử bảo trì của chi nhánh mà admin đang quản lý
     * - Có phân trang, filter theo pitchName, startFrom, endTo
     * - Chỉ dành cho ADMIN_BRANCH
     */
    @GetMapping("/history")
    public ResponseEntity<PaginatedResponse<MaintenanceWindowFilterResponse>> getBranchMaintenanceHistory(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @ModelAttribute MaintenanceWindowFilterRequest req
    ) {
        UUID adminId = userDetails.getId();
        PaginatedResponse<MaintenanceWindowFilterResponse> response =
                maintenanceWindowHistoryService.getBranchMaintenanceHistory(adminId, req);
        return ResponseEntity.ok(response);
    }

    /**
     *  Xóa 1 maintenance window
     * - Chỉ cho phép admin branch xóa maintenance thuộc chi nhánh mình quản lý
     * - Không được xóa nếu thời gian đã hoặc đang diễn ra
     */
    @DeleteMapping("/{maintenanceId}")
    public ResponseEntity<MaintenanceWindowDeleteResponse> deleteMaintenanceWindow(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable UUID maintenanceId
    ) {
        UUID adminId = userDetails.getId();
        MaintenanceWindowDeleteRequest req = new MaintenanceWindowDeleteRequest();
        req.setMaintenanceId(maintenanceId);

        MaintenanceWindowDeleteResponse response =
                maintenanceWindowDeleteService.deleteMaintenanceWindow(adminId, req);
        return ResponseEntity.ok(response);
    }
}
