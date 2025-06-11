package com.store.api.order.model;

public enum OrderStatus {

    // 일반 주문
    REGULAR("일반 주문"),

    // 프로모션 주문
    PROMOTION("프로모션 주문"),
    INSUFFICIENT_PROMOTION_STOCK("프로모션 재고 부족"),
    PROMOTION_WITH_COMPLIMENTARY("추가 혜택 적용 프로모션 주문"),

    // 예외
    OUT_OF_STOCK("재고 부족")
    ;

    private final String message;

    OrderStatus(String message) {
        this.message = message;
    };

    public String message() {
        return message;
    }
}
