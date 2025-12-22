package com.example.footballmanagement.service.impl;

import java.time.OffsetDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.footballmanagement.constant.Endpoint.BASE;
import com.example.footballmanagement.dto.request.PasswordResetRequest;
import com.example.footballmanagement.dto.response.PasswordResetResponse;
import com.example.footballmanagement.entity.AccountRecovery;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.RecoveryStatus;
import com.example.footballmanagement.repository.AccountRecoveryRepository;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.AccountRecoveryService;
import com.example.footballmanagement.service.NotificationService;
import com.example.footballmanagement.utils.TokenGenerator;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class AccountRecoveryServiceImpl implements AccountRecoveryService {

    private final UserRepository userRepository;
    private final AccountRecoveryRepository accountRecoveryRepository;
    private final NotificationService emailService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public PasswordResetResponse initiateRecovery(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));
         // clear token cũ
        accountRecoveryRepository.deleteByUser_Id(user.getId());

        String token = TokenGenerator.generateToken(30);

        AccountRecovery recovery = AccountRecovery.builder()
                .user(user)
                .recoveryToken(token)
                .expiresAt(OffsetDateTime.now().plusMinutes(15))
                .status(RecoveryStatus.PENDING)
                .build();

        accountRecoveryRepository.save(recovery);

        String resetLink = BASE + "/resetpassword?token=" + token;

        emailService.sendSimpleMessage(
                user.getEmail(),
                "Password Recovery",
                "Click this link to reset your password: " + resetLink
        );
        return new PasswordResetResponse("Recovery email sent", 200);
    }

    @Override
    @Transactional
    public PasswordResetResponse confirmRecovery(String token, PasswordResetRequest request) {
        AccountRecovery recovery = accountRecoveryRepository.findByRecoveryToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        
        if (recovery.getExpiresAt().isBefore(OffsetDateTime.now())) {
            recovery.setStatus(RecoveryStatus.EXPIRED);
            accountRecoveryRepository.save(recovery);
            throw new IllegalStateException("Token expired");
        }

        User user = recovery.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        recovery.setStatus(RecoveryStatus.USED);
        accountRecoveryRepository.save(recovery);
        return new PasswordResetResponse("Password reset successfully", 200);
    }
    }

