package com.store.api.common.exception;

public enum ExceptionCode {
    // sale product
    OUT_OF_STOCK("SP001", "재고가 부족합니다."),
    INSUFFICIENT_REGULAR_STOCK("SP002", "일반 상품 재고가 부족합니다."),
    INSUFFICIENT_PROMOTION_STOCK("SP003", "프로모션 상품 재고가 부족합니다."),
    // promotion
    CAN_GET_COMPLIMENTARY_QUANTITY("PR001", "프로모션 추가 혜택 구매 가능합니다."),
    // order
    INVALID_ORDER_QUANTITY("OR001", "주문 수량이 0 이상이어야합니다.");

    private final String code;
    private final String message;

    ExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String code() {
        return code;
    }
}