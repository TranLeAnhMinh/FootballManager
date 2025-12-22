package com.example.footballmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceWindowDeleteResponse {
    private UUID id;
    private UUID pitchId;
    private String pitchName;
    private String pitchLocation;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private OffsetDateTime deletedAt;
    private String deletedBy; // 👈 tên admin branch thực hiện xoá
}
