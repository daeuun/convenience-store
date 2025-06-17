package com.store.api.order.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderValidateRequest {
    private final List<OrderDetail> orderDetails;

    @JsonCreator
    public OrderValidateRequest(@JsonProperty("orderDetails") List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
