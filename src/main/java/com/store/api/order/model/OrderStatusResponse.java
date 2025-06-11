package com.store.api.order.model;

import lombok.Getter;

@Getter
public class OrderStatusResponse {
    private OrderDetail orderDetail;
    private OrderStatus orderStatus;

    private OrderStatusResponse(OrderDetail orderDetail, OrderStatus orderStatus) {
        this.orderDetail = orderDetail;
        this.orderStatus = orderStatus;
    }

    public static OrderStatusResponse toResponse(OrderDetail orderDetail, OrderStatus orderStatus) {
        return new OrderStatusResponse(orderDetail, orderStatus);
    }
}
