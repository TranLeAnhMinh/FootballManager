package com.example.footballmanagement.dto.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String role;     // USER / ADMIN_BRANCH / PENDING_ADMIN_BRANCH
    private String status;   // ACTIVE / INACTIVE
    private String branchName; // null nếu chưa thuộc branch
}
