package com.example.footballmanagement.utils;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.footballmanagement.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    public String generateAccessToken(UUID userId, String subjectEmail, String role, UUID sessionId) {
        return baseBuilder(userId, subjectEmail, role, sessionId.toString(), jwtProperties.getAccessExpMs()).compact();
    }

    public String generateRefreshToken(UUID userId, String subjectEmail, String role, UUID sessionId) {
        return baseBuilder(userId, subjectEmail, role, sessionId.toString(), jwtProperties.getRefreshExpMs()).compact();
    }

    
    private JwtBuilder baseBuilder(UUID userId, String email, String role, String sessionId, long ttl) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttl))
                .addClaims(Map.of(
                        "uid", userId.toString(),
                        "role", role,
                        "sid", sessionId
                ))
                .signWith(key(), SignatureAlgorithm.HS256);
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token);
    }

    public String getEmail(String token) {
        return parse(token).getBody().getSubject();
    }

    public String getRole(String token) {
        return (String) parse(token).getBody().get("role");
    }

    public UUID getSessionId(String token) {
        String sid = (String) parse(token).getBody().get("sid");
        return UUID.fromString(sid);
    }

    public boolean isExpired(String token) {
        return parse(token).getBody().getExpiration().before(new Date());
    }
}
