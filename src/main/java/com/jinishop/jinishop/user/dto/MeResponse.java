package com.jinishop.jinishop.user.dto;

import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeResponse {
    // 현재 로그인한 회원 응답 DTO

    private Long id;
    private String email;
    private String name;
    private UserRole role;

    public MeResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
    }
}