package com.example.footballmanagement.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResponse{
    private final String accessToken;
    private final String refreshToken;
    private final UUID userId;
    private final String fullName;
    private final String role;
}