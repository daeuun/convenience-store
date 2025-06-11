package com.store.api.order.resource;

import com.store.api.order.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private final OrderMapper orderMapper;

    public OrderRepository(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public void save(Order order) {
        orderMapper.insert(order);
    }
}
