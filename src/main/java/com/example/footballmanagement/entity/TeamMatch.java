package com.example.footballmanagement.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.TeamMatchStatus;

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
    name = "team_matches",
    indexes = {
        @Index(name = "idx_team_matches_teams", columnList = "team_a_id, team_b_id"),
        @Index(name = "idx_team_matches_status", columnList = "status"),
        @Index(name = "idx_team_matches_booking", columnList = "booking_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMatch {

    @Id
    @GeneratedValue
    private UUID id;

    /* ================= Associations ================= */
    @ManyToOne(optional = false)
    @JoinColumn(name = "team_a_id", nullable = false)
    private Team teamA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_b_id", nullable = false)
    private Team teamB;

    @ManyToOne
    @JoinColumn(name = "pitch_id")
    private Pitch pitch;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    /* ================= Columns ================= */
    @Column(name = "start_at")
    private OffsetDateTime startAt;

    @Column(name = "end_at")
    private OffsetDateTime endAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamMatchStatus status = TeamMatchStatus.PENDING;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
