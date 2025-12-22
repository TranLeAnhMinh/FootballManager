package com.example.footballmanagement.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "pitch_types",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_pitch_type_name", columnNames = "name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchType {

    @Id
    @Column(columnDefinition = "SMALLINT")
    private Short id;   // ví dụ 5, 7, 11

    @Column(nullable = false, length = 20, unique = true)
    private String name;  // ví dụ "5-a-side"

    /* ================= Associations ================= */

    // 1 loại sân có thể có nhiều sân (pitches)
    @OneToMany(mappedBy = "pitchType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pitch> pitches;

    // 1 loại sân có thể gắn vào nhiều bài đăng kèo (team_match_posts)
    @OneToMany(mappedBy = "pitchType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMatchPost> teamMatchPosts;
}
