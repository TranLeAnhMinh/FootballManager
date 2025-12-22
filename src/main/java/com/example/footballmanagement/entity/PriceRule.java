package com.example.footballmanagement.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    name = "price_rules",
    indexes = {
        @Index(name = "idx_price_rules_pitch_active", columnList = "pitch_id, active"),
        // GIN index cho JSONB thì giữ ở script SQL, Hibernate không hỗ trợ
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRule {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pitch_id")
    private Pitch pitch;  // rule có thể NULL = toàn hệ thống

    @Builder.Default
    @Column(nullable = false)
    private int priority = 0;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    // JSONB mapping: Hibernate 6 hỗ trợ @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String conditions;

    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String effect;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
