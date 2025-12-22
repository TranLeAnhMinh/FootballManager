package com.example.footballmanagement.entity;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "branches",
    indexes = {
        @Index(name = "idx_branches_name", columnList = "name"),
        @Index(name = "idx_branches_active", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 1 branch chỉ có 1 admin (user)
    @OneToOne
    @JoinColumn(name = "admin_id", unique = true)
    private User admin;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    /* ================= Associations ================= */

    // 1 branch có nhiều pitches
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pitch> pitches;

    // 1 branch có nhiều bookings
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    // 1 branch có nhiều teams chọn làm home_branch
    @OneToMany(mappedBy = "homeBranch", cascade = CascadeType.ALL)
    private List<Team> teams;
}
