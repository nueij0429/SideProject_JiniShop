package com.jinishop.jinishop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.global.response.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint { // Security가 인증 실패 시 자동으로 이 클래스를 호출

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        // JwtAuthenticationFilter에서 던진 BusinessException을 가져옴
        Object ex = request.getAttribute("authException");

        ErrorCode errorCode;
        // INVALID_TOKEN, TOKEN_EXPIRED 등 정확한 에러코드를 설정
        if (ex instanceof BusinessException businessException) {
            errorCode = businessException.getErrorCode(); // INVALID_TOKEN, TOKEN_EXPIRED 등
        } else {
            errorCode = ErrorCode.UNAUTHORIZED; // BusinessException이 아닌 경우 기본 401 처리
        }

        ResponseDto<?> body = ResponseDto.error(
                errorCode.getStatus().value(),
                errorCode.getMessage()
        );

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = objectMapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}