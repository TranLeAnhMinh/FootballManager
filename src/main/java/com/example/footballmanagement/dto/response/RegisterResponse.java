package com.example.footballmanagement.dto.response;

import com.example.footballmanagement.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
}