package com.example.footballmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceWindowFilterResponse {
    private UUID id;

    private UUID pitchId;
    private String pitchName;
    private String pitchLocation;
    private String pitchTypeName;

    private String reason;

    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private OffsetDateTime createdAt;
}
