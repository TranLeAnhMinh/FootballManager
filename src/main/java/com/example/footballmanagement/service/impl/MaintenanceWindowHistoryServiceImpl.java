package com.example.footballmanagement.service.impl;

import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.paginated.PaginatedResponse;
import com.example.footballmanagement.dto.request.MaintenanceWindowFilterRequest;
import com.example.footballmanagement.dto.response.MaintenanceWindowFilterResponse;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.UserStatus;
import com.example.footballmanagement.exception.ApiException;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.repository.MaintenanceWindowRepository;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.MaintenanceWindowHistoryService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceWindowHistoryServiceImpl implements MaintenanceWindowHistoryService {

    private final UserRepository userRepo;
    private final MaintenanceWindowRepository maintenanceRepo;

    @Override
    public PaginatedResponse<MaintenanceWindowFilterResponse> getBranchMaintenanceHistory(
            UUID adminId,
            MaintenanceWindowFilterRequest req
    ) {
        // 1️⃣ Kiểm tra user tồn tại
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 2️⃣ Kiểm tra quyền
        if (admin.getRole() != UserRole.ADMIN_BRANCH)
            throw new ApiException(ErrorCode.PERMISSION_DENIED);
        if (admin.getStatus() != UserStatus.ACTIVE)
            throw new ApiException(ErrorCode.USER_INACTIVE);
        if (admin.getBranchAdmin() == null)
            throw new ApiException(ErrorCode.BRANCH_NOT_FOUND);

        UUID branchId = admin.getBranchAdmin().getId();

        // 3️⃣ Tạo pageable
        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize());

        // 4️⃣ Gọi repository
        Page<com.example.footballmanagement.entity.MaintenanceWindow> page =
                maintenanceRepo.findByBranchWithFilter(
                        branchId,
                        req.getPitchName(),
                        req.getStartFrom(),
                        req.getEndTo(),
                        pageable
                );

        // 5️⃣ Map sang DTO
        var content = page.getContent().stream()
                .map(ConverterUtil::toMaintenanceWindowFilterResponse)
                .collect(Collectors.toList());

        // 6️⃣ Gói vào PaginatedResponse
        PaginatedResponse<MaintenanceWindowFilterResponse> response = new PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());

        return response;
    }
}
