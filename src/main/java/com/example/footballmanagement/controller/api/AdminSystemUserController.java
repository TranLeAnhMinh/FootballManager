package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.response.AdminUserResponse;
import com.example.footballmanagement.service.BranchAdminsystemService;
import com.example.footballmanagement.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adminsystem/users")
@RequiredArgsConstructor
public class AdminSystemUserController {

    private final UserService userService;
     private final BranchAdminsystemService branchAdminsystemService;

    /**
     * Toggle ACTIVE <-> INACTIVE user status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(
            @PathVariable("id") UUID targetUserId,
            @AuthenticationPrincipal JwtUserDetails adminDetails) {

        UUID adminId = adminDetails.getId();

        userService.toggleUserStatus(targetUserId, adminId);

        return ResponseEntity.ok("User status toggled successfully.");
    }

    /**
     * Approve a pending admin-branch user and assign to a branch
     */
    @PatchMapping("/{userId}/approve-branch/{branchId}")
    public ResponseEntity<String> approvePendingAdmin(
            @PathVariable UUID userId,
            @PathVariable UUID branchId,
            @AuthenticationPrincipal JwtUserDetails adminDetails
    ) {
        UUID adminId = adminDetails.getId();
        branchAdminsystemService.approvePendingAdmin(adminId, userId, branchId);
        return ResponseEntity.ok("Pending admin approved and assigned to the branch.");
    }
    /**
 * Get paginated users with filtering (role, status, name, email)
 */
@GetMapping
public ResponseEntity<Page<AdminUserResponse>> getUsers(
        @RequestParam(required = false) com.example.footballmanagement.entity.enums.UserRole role,
        @RequestParam(required = false) com.example.footballmanagement.entity.enums.UserStatus status,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
) {
    Pageable pageable = PageRequest.of(page, size);

    Page<com.example.footballmanagement.entity.User> users =
            userService.searchUsers(role, status, name, email, pageable);

    Page<AdminUserResponse> dtoPage = users.map(u -> {
        AdminUserResponse dto = new AdminUserResponse();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole().name());
        dto.setStatus(u.getStatus().name());
        dto.setBranchName(
            u.getBranchAdmin() != null
                ? u.getBranchAdmin().getName()
                : null
        );
        return dto;
    });

    return ResponseEntity.ok(dtoPage);
}
@GetMapping("/branches/available")
public ResponseEntity<?> getAvailableBranches() {
    return ResponseEntity.ok(
        branchAdminsystemService.getAvailableBranches()
            .stream()
            .map(b -> java.util.Map.of(
                "id", b.getId(),
                "name", b.getName()
            ))
            .toList()
    );
}
}
