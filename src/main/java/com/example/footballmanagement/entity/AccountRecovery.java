package com.example.footballmanagement.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.RecoveryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "account_recoveries",
    indexes = {
        @Index(name = "idx_acc_rec_user", columnList = "user_id"),
        @Index(name = "idx_acc_rec_exp", columnList = "expires_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRecovery {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recovery_token", nullable = false, unique = true, length = 255)
    private String recoveryToken;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecoveryStatus status;
}
