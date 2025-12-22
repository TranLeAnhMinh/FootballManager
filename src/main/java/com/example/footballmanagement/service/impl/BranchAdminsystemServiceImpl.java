package com.example.footballmanagement.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.entity.Branch;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.exception.ApiException;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.repository.BranchRepository;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.BranchAdminsystemService;
import com.example.footballmanagement.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchAdminsystemServiceImpl implements BranchAdminsystemService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void approvePendingAdmin(UUID adminSystemId, UUID userId, UUID branchId) {

        // 1) validate admin system
        User adminSystem = userRepository.findById(adminSystemId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (adminSystem.getRole() != UserRole.ADMIN_SYSTEM) {
            throw new ApiException(ErrorCode.ROLE_FORBIDDEN);
        }

        // 2) validate target user
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (target.getRole() != UserRole.PENDING_ADMIN_BRANCH) {
            throw new ApiException(ErrorCode.ACTION_NOT_ALLOWED);  // hoặc define code mới
        }

        // 3) validate branch
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ApiException(ErrorCode.BRANCH_NOT_FOUND));

        // 4) branch đã có admin rồi?
        if (branch.getAdmin() != null) {
            throw new ApiException(ErrorCode.BRANCH_ALREADY_HAVE_ADMIN);
        }

        // 5) update role user -> ADMIN_BRANCH
        target.setRole(UserRole.ADMIN_BRANCH);
        userRepository.save(target);

        // 6) assign user as admin for branch
        branch.setAdmin(target);
        branchRepository.save(branch);

        // 7) gửi email confirm
        notificationService.sendSimpleMessage(
                target.getEmail(),
                "Your Admin Branch Request Approved",
                "Hello " + target.getFullName() + ",\n\n" +
                "Your request to become an ADMIN BRANCH has been approved.\n" +
                "You are now assigned as admin of branch: " + branch.getName() + "\n\n" +
                "You may now log in and manage your branch.\n" +
                "Thank you."
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Branch> getAvailableBranches() {
        return branchRepository.findAvailableForAssign();
    }
}
