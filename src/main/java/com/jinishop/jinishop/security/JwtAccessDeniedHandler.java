package com.jinishop.jinishop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.global.response.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler { // 권한 부족(Authorization 실패) 시 호출

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        ErrorCode errorCode = ErrorCode.FORBIDDEN; // 인가 실패는 항상 FORBIDDEN

        ResponseDto<?> body = ResponseDto.error(
                errorCode.getStatus().value(),
                errorCode.getMessage()
        );

        response.setStatus(errorCode.getStatus().value()); // HTTP 상태 코드 403 반환
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = objectMapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}