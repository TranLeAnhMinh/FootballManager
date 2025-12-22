package com.example.footballmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchCreateRequest {

    private String name;        // Tên chi nhánh
    private String location;    // Địa chỉ cụ thể
    private String description; // Mô tả thêm (nếu có)
}
