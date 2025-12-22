package com.example.footballmanagement.dto.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceWindowDeleteRequest {
    private UUID maintenanceId;  // ID của MaintenanceWindow cần xoá
}
