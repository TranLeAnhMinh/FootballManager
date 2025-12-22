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
public class PitchesFilterRequestDto {
    private String branchName;   // lọc theo tên chi nhánh (tùy chọn)
    private String pitchName;    // lọc theo tên sân (tùy chọn)
    private Boolean active;      // lọc theo trạng thái hoạt động
}
