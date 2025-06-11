package com.store.api.order.resource;

import com.store.api.order.model.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {

    @Insert("""
    <script>
    INSERT INTO order_item(order_id, product_id, product_name, price, quantity, promotion_quantity, discount_price)
    VALUES (
    #{orderId},
    #{productId},
    #{productName},
    #{price},
    #{quantity},
    #{promotionQuantity},
    #{discountPrice}
    )
    </script>
    """)
    void insert(OrderItem orderItem);
}
