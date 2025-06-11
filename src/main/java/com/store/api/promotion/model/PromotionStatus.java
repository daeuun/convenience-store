package com.store.api.promotion.model;

public enum PromotionStatus {
    WITHOUT_PROMOTION("일반 주문"),
    PROMOTION_ORDER("프로모션 주문"),
    INSUFFICIENT_PROMOTION_STOCK("일부 정가 구매"),
    PROMOTION_COMPLIMENTARY("프로모션 추가 혜택");

    private final String status;

    PromotionStatus(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
