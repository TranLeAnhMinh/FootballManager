package com.example.footballmanagement.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.dto.request.PasswordResetRequest;
import com.example.footballmanagement.dto.response.PasswordResetResponse;
import com.example.footballmanagement.service.AccountRecoveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/recover")
@RequiredArgsConstructor
public class AccountRecoveryController {

    private final AccountRecoveryService accountRecoveryService;

    @PostMapping
    public ResponseEntity<PasswordResetResponse> initiateRecovery(@RequestParam String email) {
        return ResponseEntity.ok(accountRecoveryService.initiateRecovery(email));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PasswordResetResponse> confirmRecovery(@RequestParam String token,
                                                                 @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(accountRecoveryService.confirmRecovery(token, request));
    }
}
