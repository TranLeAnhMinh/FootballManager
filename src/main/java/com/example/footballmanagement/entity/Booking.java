package com.example.footballmanagement.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.entity.enums.BookingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "bookings",
    indexes = {
        @Index(name = "idx_bookings_user", columnList = "user_id"),
        @Index(name = "idx_bookings_pitch", columnList = "pitch_id"),
        @Index(name = "idx_bookings_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /** ✅ Giá cuối cùng (final price) đã tính lúc createBooking */
    @Column(name = "final_price", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal finalPrice;

    /* ================= Associations ================= */

    // 1 booking có nhiều slots
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSlot> slots;

    // 1 booking có nhiều payments
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    // 1 booking có thể có 1 voucher usage
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private VoucherUsage voucherUsage;

    // 1 booking có thể tham chiếu trong team_matches
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<TeamMatch> teamMatches;
}
