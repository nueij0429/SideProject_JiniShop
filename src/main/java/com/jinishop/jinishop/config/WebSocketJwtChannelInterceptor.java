package com.jinishop.jinishop.config;

import com.jinishop.jinishop.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {
    // WebSocket JWT 인증 (핸드셰이크/CONNECT에서 토큰 검증)

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);

            if (token == null || token.isBlank()) {
                throw new MessagingException("Missing JWT token");
            }

            jwtTokenProvider.validateTokenOrThrow(token);
            var authentication = jwtTokenProvider.getAuthentication(token);

            // WebSocket 세션에 인증 심기
            accessor.setUser(authentication);
        }

        return message;
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        // 1. Authorization: Bearer <token>
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String v = authHeaders.get(0);
            if (v != null && v.startsWith("Bearer ")) return v.substring(7);
        }

        // 2. 토큰을 "token" 헤더로 보낼 수도 있게
        List<String> tokenHeaders = accessor.getNativeHeader("token");
        if (tokenHeaders != null && !tokenHeaders.isEmpty()) {
            return tokenHeaders.get(0);
        }

        return null;
    }
}
