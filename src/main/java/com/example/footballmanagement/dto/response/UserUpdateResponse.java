package com.example.footballmanagement.dto.response;


import java.util.UUID;

import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserUpdateResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
}
