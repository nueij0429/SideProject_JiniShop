package com.jinishop.jinishop.user.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.security.JwtTokenProvider;
import com.jinishop.jinishop.user.domain.RefreshToken;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.dto.AuthResponse;
import com.jinishop.jinishop.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-validity-ms:1209600000}")
    private long refreshTokenValidityInMs;

    // 회원가입/로그인 시 Refresh Token 저장
    public void saveRefreshToken(User user, String refreshToken) {
        // 같은 유저의 기존 토큰 있으면 삭제 (1인 1토큰 정책)
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenValidityInMs)))
                .build();

        refreshTokenRepository.save(entity);
    }

    // Refresh 요청 시 토큰 검증 + 재발급
    public AuthResponse refresh(String refreshToken) {
        // 1) JWT 형식/만료 검증
        if (!jwtTokenProvider.validateTokenOrThrow(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2) DB에 저장된 토큰인지 확인
        RefreshToken entity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 3) DB 만료 시간도 체크
        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(entity);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = entity.getUser();

        // 4) 새 Access / Refresh 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // 5) DB의 RefreshToken 갱신 (토큰 로테이션)
        entity.setToken(newRefreshToken);
        entity.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenValidityInMs)));

        // 6) 응답으로 두 토큰 모두 반환
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    // 로그아웃 - 해당 사용자의 RefreshToken 삭제
    public void logout(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}