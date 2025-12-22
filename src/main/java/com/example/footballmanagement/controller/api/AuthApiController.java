package com.example.footballmanagement.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.dto.request.LoginRequest;
import com.example.footballmanagement.dto.request.RefreshRequest;
import com.example.footballmanagement.dto.request.RegisterRequest;
import com.example.footballmanagement.dto.response.JwtResponse;
import com.example.footballmanagement.dto.response.RegisterResponse;
import com.example.footballmanagement.service.AuthService;
import com.example.footballmanagement.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return ResponseEntity.ok(authService.login(req, http));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearer) {
        String token = bearer != null && bearer.startsWith("Bearer ") ? bearer.substring(7) : null;
        if (token != null) authService.logout(token);
        return ResponseEntity.noContent().build();
    }
    
}
