package com.example.footballmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class AppProperties {

    @Value("${app.base-url}")
    private String baseUrl;
}