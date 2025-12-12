package com.jinishop.jinishop.global.exception;

import com.jinishop.jinishop.global.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 로직에서 발생한 모든 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<?>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        ResponseDto<?> body = ResponseDto.error(
                errorCode.getStatus().value(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleException(Exception e) {
        e.printStackTrace(); // 로그 출력

        ResponseDto<?> body = ResponseDto.error(
                500,
                "서버 에러가 발생했습니다."
        );

        return ResponseEntity
                .status(500)
                .body(body);
    }
}