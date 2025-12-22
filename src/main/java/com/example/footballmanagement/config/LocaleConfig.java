package com.example.footballmanagement.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver clr = new CookieLocaleResolver();
        // ✅ Thiết lập ngôn ngữ mặc định
        clr.setDefaultLocale(Locale.ENGLISH);
        // ❌ KHÔNG dùng setCookieName vì đã deprecated và gây lỗi không đổi ngôn ngữ
        return clr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // ✅ định nghĩa tên query param để đổi ngôn ngữ (?lang=vi hoặc ?lang=en)
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("✅ LocaleChangeInterceptor registered"); // 👈 Thêm dòng này để chắc chắn nó chạy
        registry.addInterceptor(localeChangeInterceptor());
    }
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:/i18n/messages");  // 📂 thư mục chứa messages.properties
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        return ms;
    }


}
