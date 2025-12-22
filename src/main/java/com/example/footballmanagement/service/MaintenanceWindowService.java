package com.example.footballmanagement.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.dto.request.MaintenanceWindowRequest;
import com.example.footballmanagement.dto.response.BookingOverlapResponse;
import com.example.footballmanagement.dto.response.MaintenanceWindowResponse;
public interface MaintenanceWindowService {

    // ✅ Check overlap khi booking
    boolean existsOverlap(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt);

    //Kiem tra confilic
    boolean checkConflict(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt);

    // ✅ Tạo mới maintenance window (chỉ admin branch đúng chi nhánh được phép)
    MaintenanceWindowResponse createMaintenanceWindow(UUID adminId, MaintenanceWindowRequest req);
    // ✅ Gom lại thành 1 method duy nhất
    // Nếu from/to = null → lấy toàn bộ
    // Nếu from/to != null → lọc theo khoảng thời gian
    List<MaintenanceWindowResponse> getMaintenanceWindows(UUID pitchId, OffsetDateTime from, OffsetDateTime to);

    List<BookingOverlapResponse> checkOverlap(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt);

}
