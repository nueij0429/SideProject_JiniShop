package com.jinishop.jinishop.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    // 공통 응답 Wrapper

    private int status;
    private String message;
    private T data;

    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(200, "OK", data);
    }

    public static ResponseDto<?> error(int status, String message) {
        return new ResponseDto<>(status, message, null);
    }
}
