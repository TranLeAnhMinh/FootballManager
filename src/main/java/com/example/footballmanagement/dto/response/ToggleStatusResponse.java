package com.example.footballmanagement.dto.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleStatusResponse {
    private UUID userId;
    private String newStatus; // ACTIVE / INACTIVE
    private String message;
}
