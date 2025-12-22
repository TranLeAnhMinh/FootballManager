package com.example.footballmanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.footballmanagement.entity.AccountRecovery;

public interface AccountRecoveryRepository extends JpaRepository<AccountRecovery, UUID> {
    Optional<AccountRecovery> findByRecoveryToken(String token);

    void deleteByUser_Id(UUID userId); 
}
