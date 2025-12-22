package com.example.footballmanagement.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.VoucherType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "vouchers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_voucher_code", columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_vouchers_active", columnList = "active"),
        @Index(name = "idx_vouchers_time", columnList = "start_at, end_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoucherType type; // PERCENT | FIXED

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name = "max_discount", precision = 12, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "min_order", precision = 12, scale = 2)
    private BigDecimal minOrder;

    @Column(name = "start_at")
    private OffsetDateTime startAt;

    @Column(name = "end_at")
    private OffsetDateTime endAt;

    @Column(name = "per_user_limit")
    private Integer perUserLimit;   // giới hạn số lần 1 user được dùng

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
