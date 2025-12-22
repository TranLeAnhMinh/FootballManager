package com.example.footballmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingSlotResponse {
    private final UUID id;
    private final OffsetDateTime startAt;
    private final OffsetDateTime endAt;
    private final boolean checkedIn;
}
