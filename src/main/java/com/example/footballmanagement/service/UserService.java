package com.example.footballmanagement.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.footballmanagement.dto.request.RegisterRequest;
import com.example.footballmanagement.dto.request.UserUpdateRequest;
import com.example.footballmanagement.dto.response.RegisterResponse;
import com.example.footballmanagement.dto.response.UserUpdateResponse;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.UserStatus;
public interface UserService {
    RegisterResponse register(RegisterRequest request);
    UserUpdateResponse updateUser (UUID id, UserUpdateRequest request);
    UserUpdateResponse getMyInfo(UUID id);
    void toggleUserStatus(UUID targetUserId, UUID adminSystemId);
    // ================== ADDED FOR ADMIN SYSTEM SEARCH ==================
    Page<User> searchUsers(
        UserRole role,
        UserStatus status,
        String name,
        String email,
        Pageable pageable
);

}
