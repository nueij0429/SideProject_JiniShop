package com.jinishop.jinishop.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    // 로그인 요청 DTO

    private String email;
    private String password;
}