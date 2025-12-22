package com.example.footballmanagement.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.footballmanagement.service.TokenBlacklist;

@Service
public class InMemoryTokenBlacklist implements TokenBlacklist {
    private final Map<UUID, Long> revoked = new ConcurrentHashMap<>();

    @Override
    public void revokeSession(UUID sessionId, long ttlMillis) {
        revoked.put(sessionId, System.currentTimeMillis() + ttlMillis);
    }

    @Override
    public boolean isRevoked(UUID sessionId) {
        Long exp = revoked.get(sessionId);
        if (exp == null) return false;
        if (exp < System.currentTimeMillis()) {
            revoked.remove(sessionId);
            return false;
        }
        return true;
    }
}
