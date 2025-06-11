package com.store.api.order.model;

import lombok.Getter;
import org.apache.ibatis.type.Alias;

@Getter
@Alias("OrderItem")
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private final String productName;
    private final long price;
    private int quantity; // 전체수량
    private int promotionQuantity; // 증정수량
    private long discountPrice; // 증정에 따른 할인 금액

    private OrderItem(Long productId, String productName, long price, int quantity, int promotionQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.promotionQuantity = promotionQuantity;
    }

    public static OrderItem of(Long productId, String productName, long price, int quantity, int promotionQuantity) {
        return new OrderItem(productId, productName, price, quantity, promotionQuantity);
    }

    public OrderItem mappingOrder(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public long getTotalPrice() {
        return this.price * this.quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void updatePromotionDetails(int promotionQuantity) {
        this.promotionQuantity += promotionQuantity;
        long discount = price * promotionQuantity;
        this.discountPrice += discount;
    }
}