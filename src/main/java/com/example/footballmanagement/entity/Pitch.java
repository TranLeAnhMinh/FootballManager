package com.example.footballmanagement.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "pitches",
    indexes = {
        @Index(name = "idx_pitches_branch", columnList = "branch_id"),
        @Index(name = "idx_pitches_type", columnList = "pitch_type_id"),
        @Index(name = "idx_pitches_active", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pitch {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_type_id", nullable = false)
    private PitchType pitchType;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    /* ================= Associations ================= */

    // 1 sân có nhiều ảnh
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PitchImage> images;

    // 1 sân có nhiều base price
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasePrice> basePrices;

    // 1 sân có nhiều price rules
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceRule> priceRules;

    // 1 sân có nhiều booking
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    // 1 sân có nhiều booking slots
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSlot> bookingSlots;

    // 1 sân có nhiều maintenance windows
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceWindow> maintenanceWindows;

    // 1 sân có nhiều reviews
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    // 1 sân có thể tham chiếu trong team_matches
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL)
    private List<TeamMatch> teamMatches;
}
