package com.store.api.order.model;

import lombok.Getter;

@Getter
public class FinalizedOrderDetailRequest {
    private OrderDetail orderDetail;
    private OrderStatus orderStatus;
    private boolean acceptInsufficientStockOrder;
    private boolean acceptComplimentary;
}
