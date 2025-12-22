package com.example.footballmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.footballmanagement.constant.Endpoint.ADMIN_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.AUTH_LOGIN;
import static com.example.footballmanagement.constant.Endpoint.AUTH_LOGOUT;
import static com.example.footballmanagement.constant.Endpoint.AUTH_RECOVERY;
import static com.example.footballmanagement.constant.Endpoint.AUTH_RECOVERY_CONFIRM;
import static com.example.footballmanagement.constant.Endpoint.AUTH_REFRESH;
import static com.example.footballmanagement.constant.Endpoint.AUTH_REGISTER;
import static com.example.footballmanagement.constant.Endpoint.BOOKING_ADMIN_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.BOOKING_BRANCH_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.BOOKING_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.BOOKING_SLOT_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.BRANCH_ADMIN_SYSTEM_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.FORGOTPASSWORD_PAGE;
import static com.example.footballmanagement.constant.Endpoint.LOGIN_PAGE;
import static com.example.footballmanagement.constant.Endpoint.MAINTENANCE_WINDOW_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.PAYMENT_RETURN;
import static com.example.footballmanagement.constant.Endpoint.PITCH_ADMIN_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.PITCH_ADMIN_SYSTEM_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.PITCH_DETAIL_PAGE;
import static com.example.footballmanagement.constant.Endpoint.PITCH_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.REGISTER_PAGE;
import static com.example.footballmanagement.constant.Endpoint.RESETPASSWORD_PAGE;
import static com.example.footballmanagement.constant.Endpoint.REVENUE_ADMIN_SYSTEM_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.REVENUE_BRANCH_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.USER_ADMIN_SYSTEM_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.VOUCHER_ADMIN_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.VOUCHER_ADMIN_SYSTEM_ENDPOINT;
import static com.example.footballmanagement.constant.Endpoint.VOUCHER_ENDPOINT;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                /* ========= STATIC & PUBLIC ========= */
                .requestMatchers(
                        AUTH_LOGIN,
                        AUTH_REGISTER,
                        LOGIN_PAGE,
                        REGISTER_PAGE,
                        AUTH_REFRESH,
                        AUTH_LOGOUT,
                        AUTH_RECOVERY,
                        AUTH_RECOVERY_CONFIRM,
                        FORGOTPASSWORD_PAGE,
                        RESETPASSWORD_PAGE,
                        PITCH_ENDPOINT,
                        PITCH_DETAIL_PAGE,
                        MAINTENANCE_WINDOW_ENDPOINT,
                        BOOKING_SLOT_ENDPOINT,
                        PAYMENT_RETURN,
                        "/payments/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/fonts/**"
                ).permitAll()

                /* ========= PUBLIC USER PAGES ========= */
                .requestMatchers("/user/**", "/user/**?lang=**").permitAll()

                /* ========= PUBLIC ADMIN BRANCH PAGES ========= */
                // 👉 Những trang HTML render từ Thymeleaf (chỉ hiển thị giao diện)
                .requestMatchers(
                    "/admin/home",
                    "/admin/home?lang=**",
                    "/adminsystem/home?lang=**",
                    "/admin/maintenance/**",
                    "/admin/bookings",
                    "/admin/bookings/**",
                    "/admin/revenue",
                    "/adminsystem/home",
                    "/admin/revenue/**",
                    "/adminsystem/branches",
                    "/adminsystem/branches?lang=**",
                    "/adminsystem/pitches/**",
                    "/adminsystem/pitches/**?lang=**",
                    "/adminsystem/vouchers",
                    "/adminsystem/vouchers?lang=**",
                    "/adminsystem/users",
                    "/adminsystem/users?lang=**"
                ).permitAll()

                /* ========= API ========= */
                .requestMatchers("/api/payment/**").hasRole("USER")

                /* ========= ADMIN SYSTEM ========= */
                // .requestMatchers(ADMIN_SYSTEM_ENDPOINT).hasRole("ADMIN_SYSTEM")
                .requestMatchers(VOUCHER_ADMIN_ENDPOINT).hasRole("ADMIN_SYSTEM")
                .requestMatchers(ADMIN_ENDPOINT).hasRole("ADMIN_BRANCH")
                .requestMatchers(PITCH_ADMIN_SYSTEM_ENDPOINT).hasRole("ADMIN_SYSTEM")
                .requestMatchers(BRANCH_ADMIN_SYSTEM_ENDPOINT).hasRole("ADMIN_SYSTEM")
                .requestMatchers("/api/adminsystem/images/**").hasRole("ADMIN_SYSTEM")
                .requestMatchers(VOUCHER_ADMIN_SYSTEM_ENDPOINT).hasRole("ADMIN_SYSTEM")
                .requestMatchers(REVENUE_ADMIN_SYSTEM_ENDPOINT).hasRole("ADMIN_SYSTEM")
                .requestMatchers(USER_ADMIN_SYSTEM_ENDPOINT).hasRole("ADMIN_SYSTEM") 


                /* ========= ADMIN BRANCH API ========= */
                .requestMatchers(PITCH_ADMIN_ENDPOINT).hasRole("ADMIN_BRANCH")
                .requestMatchers(BOOKING_BRANCH_ENDPOINT).hasRole("ADMIN_BRANCH")
                .requestMatchers(BOOKING_ADMIN_ENDPOINT).hasRole("ADMIN_BRANCH")
                .requestMatchers(REVENUE_BRANCH_ENDPOINT).hasRole("ADMIN_BRANCH")
                .requestMatchers("/api/admin/maintenance-windows/**").hasRole("ADMIN_BRANCH")

                /* ========= USER API ========= */
                .requestMatchers(BOOKING_ENDPOINT).hasRole("USER")
                .requestMatchers(VOUCHER_ENDPOINT).hasRole("USER")

           

                /* ========= ALL OTHER ========= */
                .anyRequest().authenticated()
            )

            /* ========= Session Stateless ========= */
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            /* ========= JWT Filter ========= */
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
