package com.store.api.product.model;

public enum ProductStatus {

    WAIT("대기중"),
    SALE("판매중"),
    OUT_OF_STOCK("품절"),
    DELETE("삭제");

    private final String message;

    ProductStatus(String message) {
        this.message = message;
    };

    public String message() {
        return message;
    }
}
