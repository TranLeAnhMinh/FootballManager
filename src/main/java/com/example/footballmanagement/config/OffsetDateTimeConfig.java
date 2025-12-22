package com.example.footballmanagement.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OffsetDateTimeConfig implements WebMvcConfigurer {

    @Override
    @SuppressWarnings("Convert2Lambda")
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, OffsetDateTime>() {
            @Override
            public OffsetDateTime convert(String source) {
                return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        });
    }
}
