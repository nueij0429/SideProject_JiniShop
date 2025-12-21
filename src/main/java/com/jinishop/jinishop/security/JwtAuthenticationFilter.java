package com.jinishop.jinishop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.jinishop.jinishop.global.exception.BusinessException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // 매 요청마다 JWT 검사
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        System.out.println("[JWT] uri=" + request.getRequestURI()
                + ", hasToken=" + (token != null)
                + ", cookieHeader=" + request.getHeader("Cookie"));

        try {
            if (StringUtils.hasText(token)) {
                jwtTokenProvider.validateTokenOrThrow(token);
                var authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("[JWT] auth=" + authentication.getAuthorities());
            }
        } catch (BusinessException ex) {
            System.out.println("[JWT] fail code=" + ex.getErrorCode());
            // 인증 관련 예외를 request에 태워두고, 실제 응답은 EntryPoint에서 처리
            request.setAttribute("authException", ex);
        }

        filterChain.doFilter(request, response);
    }

    // Authorization: Bearer <token> 에서 토큰만 추출
    private String resolveToken(HttpServletRequest request) {
        // 1. Authorization 헤더 우선
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // 2. access_token 쿠키 fallback
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/favicon.ico")
                || uri.startsWith("/.well-known/")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/webjars/");
    }
}
