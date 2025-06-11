package com.store.api.order.resource;

import com.store.api.order.model.OrderItem;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepository {

    private final OrderItemMapper orderItemMapper;

    public OrderItemRepository(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public void saveAll(List<OrderItem> orderItems) {
        orderItems.forEach(orderItemMapper::insert);
    }
}
