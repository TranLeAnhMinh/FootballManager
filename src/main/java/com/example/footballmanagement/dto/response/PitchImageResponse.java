package com.example.footballmanagement.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchImageResponse {
    private UUID id;
    private String url;
    private boolean isCover; 
}