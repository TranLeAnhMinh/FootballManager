package com.example.footballmanagement.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MaintenanceWindowRequest {
    private UUID pitchId;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private String reason;
}
