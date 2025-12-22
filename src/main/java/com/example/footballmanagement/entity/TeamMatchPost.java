package com.example.footballmanagement.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.TeamMatchPostStatus;

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
    name = "team_match_posts",
    indexes = {
        @Index(name = "idx_tmp_open", columnList = "status"),
        @Index(name = "idx_tmp_date_time", columnList = "preferred_date, time_start, time_end"),
        @Index(name = "idx_tmp_type", columnList = "pitch_type_id"),
        @Index(name = "idx_tmp_branch", columnList = "branch_id"),
        @Index(name = "idx_tmp_skill", columnList = "skill_min, skill_max")
    }
    // ⚠️ unique partial index uniq_team_open_per_day chỉ tạo trong SQL script, Hibernate không hỗ trợ
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMatchPost {

    @Id
    @GeneratedValue
    private UUID id;

    /* ================= Associations ================= */
    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_type_id", nullable = false)
    private PitchType pitchType;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private TeamMatch match;

    /* ================= Columns ================= */
    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

    @Column(name = "skill_min")
    private Integer skillMin;

    @Column(name = "skill_max")
    private Integer skillMax;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamMatchPostStatus status = TeamMatchPostStatus.OPEN;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
