package com.example.footballmanagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterRequest {
    private String keyword; // search theo fullname/email/phone
    private String role;    // USER / ADMIN_BRANCH / PENDING_ADMIN_BRANCH
    private String status;  // ACTIVE / INACTIVE
}
