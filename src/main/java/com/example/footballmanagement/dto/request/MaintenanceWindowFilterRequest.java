package com.example.footballmanagement.dto.request;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceWindowFilterRequest {
    private int page = 0; // mặc định trang đầu
    private int size = 10; // mặc định 10 bản ghi/trang

    // bộ lọc (tùy chọn)
    private OffsetDateTime startFrom; // lọc theo khoảng bắt đầu
    private OffsetDateTime endTo;     // lọc theo khoảng kết thúc
    private String pitchName;         // tìm kiếm theo tên sân
}
