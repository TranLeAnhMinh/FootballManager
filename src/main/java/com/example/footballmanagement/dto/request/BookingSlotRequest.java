package com.example.footballmanagement.dto.request;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSlotRequest {
    @NotNull
    private OffsetDateTime startAt;

    @NotNull
    private OffsetDateTime endAt;
}
