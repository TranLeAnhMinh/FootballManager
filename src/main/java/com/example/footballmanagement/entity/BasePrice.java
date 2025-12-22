package com.example.footballmanagement.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "base_prices",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_base_price_pitch_day_time",
            columnNames = {"pitch_id", "day_of_week", "time_start", "time_end"}
        )
    },
    indexes = {
        @Index(name = "idx_base_prices_pitch_day", columnList = "pitch_id, day_of_week")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasePrice {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;  // 0 = Chủ nhật, 1..6 = Thứ 2..Thứ 7

    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}
