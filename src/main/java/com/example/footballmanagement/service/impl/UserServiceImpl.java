package com.example.footballmanagement.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.RegisterRequest;
import com.example.footballmanagement.dto.request.UserUpdateRequest;
import com.example.footballmanagement.dto.response.RegisterResponse;
import com.example.footballmanagement.dto.response.UserUpdateResponse;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.UserStatus;
import com.example.footballmanagement.exception.ApiException;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.exception.custom.RegisterException;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.NotificationService;
import com.example.footballmanagement.service.UserService;
import com.example.footballmanagement.utils.ConverterUtil;
import com.example.footballmanagement.utils.PasswordUtil;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

@Override
public RegisterResponse register(RegisterRequest request){
    UserRole role = request.getRole();

    if (role == null) {
        throw new ApiException(ErrorCode.ROLE_REQUIRED);
    }

    if (role == UserRole.ADMIN_SYSTEM) {
        throw new ApiException(ErrorCode.ROLE_FORBIDDEN);
    }

    // ADMIN_BRANCH → chuyển sang Pending
    boolean pendingAdmin = false;
    if (role == UserRole.ADMIN_BRANCH) {
        role = UserRole.PENDING_ADMIN_BRANCH;
        pendingAdmin = true;
    }

    if (userRepository.existsByEmail(request.getEmail())) {
        throw new RegisterException("register.error.emailExists");
    }

    String rawPassword = PasswordUtil.generateRandomPassword(8);
    String encodedPassword = passwordEncoder.encode(rawPassword);

    User user = User.builder()
        .fullName(request.getFullName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .passwordHash(encodedPassword)
        .role(role)
        .build();

    userRepository.save(user);

    if (pendingAdmin) {
        // gửi mail pending
        notificationService.sendSimpleMessage(
    user.getEmail(),
    "Request Submitted – Awaiting Admin Approval",
    "Hello " + user.getFullName() + ",\n\n" +
    "Your request to become an ADMIN BRANCH has been successfully submitted.\n" +
    "Your account is currently flagged as PENDING APPROVAL.\n\n" +
    "An Admin System will review your request shortly.\n" +
    "If your request is approved, you will be able to log in using the temporary password below:\n\n" +
    "Temporary Password: " + rawPassword + "\n\n" +
    "Until approval is granted, login access is restricted.\n" +
    "Thank you for your patience."
);

    } else {
        // gửi mail user thường
        notificationService.sendSimpleMessage(
            user.getEmail(),
            "Your Account Password",
            "Hello " + user.getFullName() + ",\n\n" +
                    "Your account has been created successfully.\n" +
                    "Your password is: " + rawPassword + "\n\n" +
                    "Please change it after logging in."
        );
    }

    return RegisterResponse.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .role(user.getRole())
        .build();
}


    @Override
    @Transactional
    public UserUpdateResponse updateUser(UUID id, UserUpdateRequest request){
        // Tìm user theo id
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
         // Check trùng số điện thoại (nếu có nhập phone mới)
        if (request.getPhone() != null 
                && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone())
                && !request.getPhone().equals(user.getPhone())) {
            throw new RuntimeException("Phone number already in use");
        }

        // Update các field cho phép
        user.setFullName(request.getFullname());
        user.setPhone(request.getPhone());

        // Save lại DB
        User updated = userRepository.save(user);

        return ConverterUtil.toUserUpdateResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserUpdateResponse getMyInfo(UUID id){
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return ConverterUtil.toUserUpdateResponse(user);
    }

    @Override
@Transactional
public void toggleUserStatus(UUID targetUserId, UUID adminSystemId) {

    // 1) xác thực admin-system
    User admin = userRepository.findById(adminSystemId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

    if (admin.getRole() != UserRole.ADMIN_SYSTEM) {
        throw new ApiException(ErrorCode.ROLE_FORBIDDEN); 
    }

    // 2) lấy user target
    User target = userRepository.findById(targetUserId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

    // 3) không cho tự disable chính mình (optional)
    if (adminSystemId.equals(targetUserId)) {
        throw new ApiException(ErrorCode.ACTION_NOT_ALLOWED);
    }

    // 4) chuyển trạng thái
    UserStatus newStatus = (target.getStatus() == UserStatus.ACTIVE)
            ? UserStatus.INACTIVE
            : UserStatus.ACTIVE;

    int updated = userRepository.updateStatus(targetUserId, newStatus);

    if (updated == 0) {
        throw new ApiException(ErrorCode.UPDATE_FAILED);
    }
}
@Override
@Transactional(readOnly = true)
public Page<User> searchUsers(
        UserRole role,
        UserStatus status,
        String name,
        String email,
        Pageable pageable
) {
    return userRepository.searchUsers(role, status, name, email, pageable);
}
}
