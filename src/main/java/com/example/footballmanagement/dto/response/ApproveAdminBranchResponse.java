package com.example.footballmanagement.dto.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveAdminBranchResponse {
    private UUID userId;
    private UUID branchId;
    private String newRole; // ADMIN_BRANCH
    private String message;
}
