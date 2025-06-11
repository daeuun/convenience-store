package com.store.api.order.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class OrderItemResponse {
    private String productName;
    private long price;
    private int quantity;
    private int promotionQuantity;
    private long discountPrice;

    private OrderItemResponse(String productName, long price, int quantity, int promotionQuantity, long discountPrice) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.promotionQuantity = promotionQuantity;
        this.discountPrice = discountPrice;
    }

    public static List<OrderItemResponse> from(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemResponse(
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPromotionQuantity(),
                        item.getDiscountPrice()
                ))
                .collect(Collectors.toList());
    }
}
