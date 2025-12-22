package com.example.footballmanagement.service.impl;


import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.footballmanagement.dto.request.LoginRequest;
import com.example.footballmanagement.dto.request.RefreshRequest;
import com.example.footballmanagement.dto.response.JwtResponse;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.UserSession;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.repository.UserSessionRepository;
import com.example.footballmanagement.service.AuthService;
import com.example.footballmanagement.service.TokenBlacklist;
import com.example.footballmanagement.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final UserSessionRepository sessionRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;
    private final TokenBlacklist blacklist;

    @Override
    public JwtResponse login(LoginRequest req, HttpServletRequest http) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // tạo phiên
        UserSession session = new UserSession();
        session.setUser(user);
        session.setLoginTime(OffsetDateTime.now());
        session.setIpAddress(getClientIp(http));
        session = sessionRepo.save(session);

        String access = jwt.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name(), session.getId());
        String refresh = jwt.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().name(), session.getId());

        return JwtResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public JwtResponse refresh(RefreshRequest req) {
        var claims = jwt.parse(req.getRefreshToken()).getBody();
        if (jwt.isExpired(req.getRefreshToken())) {
            throw new RuntimeException("Refresh token expired");
        }

        UUID sid = UUID.fromString((String) claims.get("sid"));
        if (blacklist.isRevoked(sid)) throw new RuntimeException("Session revoked");
        // phiên còn mở?
        sessionRepo.findByIdAndLogoutTimeIsNull(sid)
                .orElseThrow(() -> new RuntimeException("Session not active"));

        String email = claims.getSubject();
        String role  = (String) claims.get("role");
        User user = userRepo.findByEmail(email).orElseThrow();

        String newAccess  = jwt.generateAccessToken( user.getId(),email, role, sid);
        String newRefresh = jwt.generateRefreshToken( user.getId(),email, role, sid);

        return JwtResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(role)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        var c = jwt.parse(accessToken).getBody();
        UUID sid = UUID.fromString((String) c.get("sid")); 
        // đóng phiên trong DB
        sessionRepo.findById(sid).ifPresent(s -> {
            if (s.getLogoutTime() == null) {
                s.setLogoutTime(OffsetDateTime.now());  // ✅ OffsetDateTime
                sessionRepo.save(s);
            }
        });
        // revoke (TTL = thời gian còn lại của access token)
        long ttl = c.getExpiration().getTime() - System.currentTimeMillis();
        if (ttl < 0) ttl = 0;
        blacklist.revokeSession(sid, ttl); 
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) return ip.split(",")[0].trim();
        return request.getRemoteAddr();
    }
    
}
