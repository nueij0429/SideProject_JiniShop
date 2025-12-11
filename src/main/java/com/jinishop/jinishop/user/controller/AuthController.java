package com.jinishop.jinishop.user.controller;

import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.security.JwtTokenProvider;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.dto.AuthResponse;
import com.jinishop.jinishop.user.dto.LoginRequest;
import com.jinishop.jinishop.user.dto.SignupRequest;
import com.jinishop.jinishop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto<AuthResponse> signup(@RequestBody SignupRequest request) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userService.createUser(
                request.getEmail(),
                encodedPassword,
                request.getName()
        );

        // 회원가입 직후 Access Token 발급
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return ResponseDto.ok(new AuthResponse(token));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseDto<AuthResponse> login(@RequestBody LoginRequest request) {
        // 이메일 + 비밀번호 검증 (AuthenticationManager 사용)
        var authToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        authenticationManager.authenticate(authToken);

        // 통과하면 JWT 발급
        String token = jwtTokenProvider.generateToken(request.getEmail());

        return ResponseDto.ok(new AuthResponse(token));
    }
}