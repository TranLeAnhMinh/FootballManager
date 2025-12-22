package com.example.footballmanagement.service;

import com.example.footballmanagement.dto.request.PasswordResetRequest;
import com.example.footballmanagement.dto.response.PasswordResetResponse;

public interface AccountRecoveryService {
    PasswordResetResponse initiateRecovery(String email);
    PasswordResetResponse confirmRecovery(String token, PasswordResetRequest request);
}