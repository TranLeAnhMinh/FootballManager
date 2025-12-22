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
public class PitchImageRequest {
    private String url;        // 🔗 Link ảnh
    private boolean isCover;   // ✅ Ảnh bìa hay không
}
