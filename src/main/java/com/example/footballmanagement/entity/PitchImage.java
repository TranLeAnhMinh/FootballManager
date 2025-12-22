package com.example.footballmanagement.entity;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "pitch_images",
    indexes = {
        @Index(name = "idx_pitch_images_pitch", columnList = "pitch_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchImage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pitch_id", nullable = false)
    private Pitch pitch;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Builder.Default
    @Column(name = "is_cover", nullable = false)
    private boolean isCover = false;
}
