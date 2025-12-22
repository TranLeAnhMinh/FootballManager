package com.example.footballmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceWindowResponse {
    private UUID id;                  // id để FE map (nếu cần)
    private String reason;            // lý do bảo trì (hiển thị tooltip)
    private OffsetDateTime startAt;   // bắt đầu bảo trì
    private OffsetDateTime endAt;     // kết thúc bảo trì
}
