package com.store.api.order.model;

import java.util.List;
import lombok.Getter;

@Getter
public class OrderResponse {
    private final List<OrderItemResponse> orderItems;
    private final long totalOrderPrice;
    private final long membershipDiscountPrice;

    private OrderResponse(List<OrderItemResponse> orderItems, long totalOrderPrice, long membershipDiscountPrice) {
        this.orderItems = orderItems;
        this.totalOrderPrice = totalOrderPrice;
        this.membershipDiscountPrice = membershipDiscountPrice;
    }

    public static OrderResponse toResponse(List<OrderItemResponse> orderItems, Order order) {
        return new OrderResponse(orderItems, order.getTotalOrderPrice(), order.getMembershipDiscountPrice());
    }
}
