package com.jinishop.jinishop.user.controller;

import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.security.CustomUserDetails;
import com.jinishop.jinishop.security.JwtTokenProvider;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.dto.*;
import com.jinishop.jinishop.user.service.RefreshTokenService;
import com.jinishop.jinishop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final RefreshTokenService refreshTokenService;

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
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenService.saveRefreshToken(user, refreshToken);

        return ResponseDto.ok(new AuthResponse(accessToken, refreshToken));
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

        User user = userService.getUserByEmail(request.getEmail());

        // 통과하면 JWT 발급
        String accessToken  = jwtTokenProvider.generateAccessToken(request.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenService.saveRefreshToken(user, refreshToken);

        return ResponseDto.ok(new AuthResponse(accessToken, refreshToken));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseDto<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        AuthResponse tokens = refreshTokenService.refresh(request.getRefreshToken());
        return ResponseDto.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseDto<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 토큰에서 꺼낸 유저 id로 User 엔티티 조회
        User user = userService.getUser(userDetails.getId());
        refreshTokenService.logout(user);

        // 클라이언트에서는 받은 accessToken/refreshToken 둘 다 로컬에서 제거
        return ResponseDto.ok(null);
    }

    //  현재 로그인한 회원 정보 조회
    @GetMapping("/me")
    public ResponseDto<MeResponse> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails // CustomUserDetails 받아서 userId 추출
    ) {
        // 토큰에서 나온 id로 실제 User 엔티티 조회 - 실제 DB 값 사용
        User user = userService.getUser(userDetails.getId());
        MeResponse response = new MeResponse(user);
        return ResponseDto.ok(response);
    }
}