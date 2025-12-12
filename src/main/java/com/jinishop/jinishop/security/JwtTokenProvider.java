package com.jinishop.jinishop.security;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // application.properties에 추가
    @Value("${jwt.secret}")
    private String secret;

    // 30분
    @Value("${jwt.access-token-validity-ms}")
    private long accessTokenValidityInMs;

    // 14일
    @Value("${jwt.refresh-token-validity-ms}")
    private long refreshTokenValidityInMs;

    private final CustomUserDetailsService userDetailsService;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) { // 256bit = 32byte 이상
            throw new IllegalArgumentException("jwt.secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 발급
    public String generateAccessToken(String email) {
        return generateToken(email, accessTokenValidityInMs);
    }

    // Refresh Token 발급
    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenValidityInMs);
    }


    // 토큰 생성
    public String generateToken(String email, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        var userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    // 토큰에서 email(subject) 꺼내기
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // 토큰 유효성 검증 및 예외 처리
    public boolean validateTokenOrThrow(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            // 형식 오류 등
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}