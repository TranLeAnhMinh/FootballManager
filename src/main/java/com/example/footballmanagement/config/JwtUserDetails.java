package com.example.footballmanagement.config;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUserDetails implements UserDetails {
    private final UUID id;
    private final String email;
    private final String role;

    public JwtUserDetails(UUID id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getRole() { return role; }

    @Override
    public String getUsername() { return email; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.toUpperCase());
    }

    @Override public String getPassword() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
