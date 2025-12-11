package com.jinishop.jinishop.security;

import java.util.Collection;
import java.util.List;

import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final UserRole role;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_USER, ROLE_ADMIN 형태로 반환
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // username 대신 email 사용
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 확장 여지
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 확장 여지
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 확장 여지
    }

    @Override
    public boolean isEnabled() {
        return true; // 확장 여지
    }
}
