package com.jinishop.jinishop.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
public enum ErrorCode {
    // BAD_REQUEST = 400
    // FORBIDDEN = 403
    // NOT_FOUND = 404
    // METHOD_NOT_ALLOWED = 405
    // INTERNAL_SERVER_ERROR = 500

    // 공통
    INVALID_INPUT_VALUE(BAD_REQUEST, "잘못된 입력값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션을 찾을 수 없습니다."),
    // 상품 추가시
    PRODUCT_NAME_REQUIRED(BAD_REQUEST, "상품 이름은 필수입니다."),
    PRODUCT_PRICE_REQUIRED(BAD_REQUEST, "상품 가격은 필수입니다."),
    PRODUCT_PRICE_INVALID(BAD_REQUEST, "상품 가격은 0원보다 커야 합니다."),
    PRODUCT_STATUS_INVALID(BAD_REQUEST, "유효하지 않은 상품 상태입니다."),
    PRODUCT_NAME_DUPLICATED(BAD_REQUEST, "이미 존재하는 상품 이름입니다."),

    // 재고
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "재고 정보를 찾을 수 없습니다."),
    STOCK_NOT_ENOUGH(BAD_REQUEST, "재고가 부족합니다."),
    STOCK_ADJUST_AMOUNT_INVALID(BAD_REQUEST, "재고 조정 수량은 0이 아닌 정수여야 합니다."),

    // 장바구니
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니가 존재하지 않습니다."),
    CART_EMPTY(BAD_REQUEST, "장바구니가 비어 있습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 아이템을 찾을 수 없습니다."),

    // 주문
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),

    // 토큰
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");
    
    private final HttpStatus status;
    private final String message;

    // 각 enum 상수에 HttpStatus와 message 값을 설정하는 역할
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
