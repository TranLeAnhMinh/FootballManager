package com.example.footballmanagement.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

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
    name = "maintenance_windows",
    indexes = {
        @Index(name = "idx_maint_pitch", columnList = "pitch_id")
        // idx_maint_range (GiST index cho range) vẫn phải tạo ở script SQL
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceWindow {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    // PostgreSQL có cột range (GENERATED), Hibernate không map trực tiếp
    // Bỏ qua vì nó được DB tự tạo, không cần field trong entity.

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
