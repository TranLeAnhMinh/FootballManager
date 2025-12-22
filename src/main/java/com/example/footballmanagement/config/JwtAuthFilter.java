package com.example.footballmanagement.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.footballmanagement.repository.UserSessionRepository;
import com.example.footballmanagement.service.TokenBlacklist;
import com.example.footballmanagement.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final TokenBlacklist blacklist;
    private final UserSessionRepository sessionRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        // ✅ 1. Bỏ qua toàn bộ request public như trang user, static, login, register...
        if (path.startsWith("/user/") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith("/login") ||
            path.startsWith("/register") ||
            path.startsWith("/favicon.ico")) 
        {
            chain.doFilter(req, res);
            return;
        }

        // ✅ 2. Nếu có token thì parse và xác thực, nếu không thì bỏ qua — KHÔNG ép phải có token
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                var claims = jwt.parse(token).getBody();

                UUID sid = UUID.fromString((String) claims.get("sid"));
                UUID uid = UUID.fromString((String) claims.get("uid"));
                String email = claims.getSubject();
                String role = (String) claims.get("role");

                boolean expired = claims.getExpiration().getTime() < System.currentTimeMillis();
                boolean revoked = blacklist.isRevoked(sid);
                boolean sessionActive = sessionRepo.findByIdAndLogoutTimeIsNull(sid).isPresent();

                if (!expired && !revoked && sessionActive) {
                    JwtUserDetails userDetails = new JwtUserDetails(uid, email, role);
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                // Không set authentication nếu parse JWT lỗi → vẫn cho qua request
            }
        }

        // ✅ 3. Cho request đi tiếp dù có hay không có token
        chain.doFilter(req, res);
    }
}
