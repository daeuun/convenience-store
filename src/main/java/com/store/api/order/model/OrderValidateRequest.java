package com.store.api.order.model;

import java.util.List;
import lombok.Getter;

@Getter
public class OrderValidateRequest {
    private List<OrderDetail> orderDetails;
}
