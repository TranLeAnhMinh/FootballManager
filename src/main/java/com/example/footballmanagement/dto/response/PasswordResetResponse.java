package com.example.footballmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetResponse {
    private String message;
    private int statusCode;
}
