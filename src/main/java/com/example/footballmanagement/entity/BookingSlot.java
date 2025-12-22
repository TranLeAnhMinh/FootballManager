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
    name = "booking_slots",
    indexes = {
        @Index(name = "idx_booking_slots_pitch", columnList = "pitch_id")
        // idx_booking_slots_range (GiST index cho range) và constraint overlap phải tạo bằng SQL script
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSlot {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    // range (TSTZRANGE) là cột GENERATED trong Postgres -> không cần map ở đây

    
    @Column(name = "qr_token_hash")
    private byte[] qrTokenHash;

    @Column(name = "qr_expires_at")
    private OffsetDateTime qrExpiresAt;

    @Column(name = "checked_in_at")
    private OffsetDateTime checkedInAt;

    @Builder.Default
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
