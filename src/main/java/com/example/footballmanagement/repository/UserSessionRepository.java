package com.example.footballmanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.footballmanagement.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByIdAndLogoutTimeIsNull(UUID id);
}