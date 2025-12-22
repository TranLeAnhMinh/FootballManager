package com.example.footballmanagement.service;

import com.example.footballmanagement.dto.request.LoginRequest;
import com.example.footballmanagement.dto.request.RefreshRequest;
import com.example.footballmanagement.dto.response.JwtResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    
    JwtResponse login(LoginRequest req, HttpServletRequest http);
    JwtResponse refresh(RefreshRequest req);
    void logout(String accessToken);
}
