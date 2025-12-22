package com.example.footballmanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VnPayConfig {
    private String tmnCode;
    private String hashSecret;
    private String payUrl;
    private String returnUrl;
}
