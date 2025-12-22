package com.example.footballmanagement.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.OtpPurpose;

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
    name = "otps",
    indexes = {
        @Index(name = "idx_otps_user_purpose", columnList = "user_id, purpose"),
        @Index(name = "idx_otps_exp", columnList = "expires_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OtpPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean consumed = false;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt;
}