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
    name = "reviews",
    indexes = {
        @Index(name = "idx_reviews_pitch", columnList = "pitch_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue
    private UUID id;

    /* ================= Associations ================= */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;

    /* ================= Columns ================= */
    @Column(nullable = false)
    private short rating;   // 1..5

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
