package com.example.footballmanagement.service;

import java.util.UUID;

public interface TokenBlacklist {
    void revokeSession(UUID sessionId, long ttlMillis);
    boolean isRevoked(UUID sessionId);
}