package com.jinishop.jinishop.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    // 회원가입 요청 DTO

    private String email;
    private String password;
    private String name;
}