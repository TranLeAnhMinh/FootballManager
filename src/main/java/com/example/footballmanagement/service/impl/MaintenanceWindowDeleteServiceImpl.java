package com.example.footballmanagement.service.impl;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.MaintenanceWindowDeleteRequest;
import com.example.footballmanagement.dto.response.MaintenanceWindowDeleteResponse;
import com.example.footballmanagement.entity.MaintenanceWindow;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.UserStatus;
import com.example.footballmanagement.exception.ApiException;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.repository.MaintenanceWindowRepository;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.MaintenanceWindowDeleteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceWindowDeleteServiceImpl implements MaintenanceWindowDeleteService {

    private final MaintenanceWindowRepository maintenanceRepo;
    private final UserRepository userRepo;

    @Override
    public MaintenanceWindowDeleteResponse deleteMaintenanceWindow(UUID adminId, MaintenanceWindowDeleteRequest req) {
        // 1️⃣ Lấy user hiện tại
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 2️⃣ Kiểm tra role + status + branch
        if (admin.getRole() != UserRole.ADMIN_BRANCH)
            throw new ApiException(ErrorCode.PERMISSION_DENIED);
        if (admin.getStatus() != UserStatus.ACTIVE)
            throw new ApiException(ErrorCode.USER_INACTIVE);
        if (admin.getBranchAdmin() == null)
            throw new ApiException(ErrorCode.BRANCH_NOT_FOUND);

        UUID branchId = admin.getBranchAdmin().getId();

        // 3️⃣ Lấy maintenance window cần xóa
        MaintenanceWindow window = maintenanceRepo.findById(req.getMaintenanceId())
                .orElseThrow(() -> new ApiException(ErrorCode.MAINTENANCE_NOT_FOUND));

        // 4️⃣ Kiểm tra quyền sở hữu (admin phải thuộc branch chứa sân này)
        Pitch pitch = window.getPitch();
        if (pitch.getBranch() == null
                || pitch.getBranch().getId() == null
                || !pitch.getBranch().getId().equals(branchId)) {
            throw new ApiException(ErrorCode.PERMISSION_DENIED);
        }

        // 5️⃣ Không cho xóa nếu đã hoặc đang diễn ra
        if (!window.getStartAt().isAfter(OffsetDateTime.now())) {
            throw new ApiException(ErrorCode.MAINTENANCE_ALREADY_STARTED);
        }

        // 6️⃣ Tiến hành xóa
        maintenanceRepo.delete(window);

        // 7️⃣ Chuẩn bị response
        MaintenanceWindowDeleteResponse res = new MaintenanceWindowDeleteResponse();
        res.setId(window.getId());
        res.setPitchId(pitch.getId());
        res.setPitchName(pitch.getName());
        res.setPitchLocation(pitch.getLocation());
        res.setStartAt(window.getStartAt());
        res.setEndAt(window.getEndAt());
        res.setDeletedAt(OffsetDateTime.now());
        res.setDeletedBy(admin.getFullName());
        return res;
    }
}
