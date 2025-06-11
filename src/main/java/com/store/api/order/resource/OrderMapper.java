package com.store.api.order.resource;

import com.store.api.order.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrderMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("""
    <script>
    INSERT INTO `order`(total_order_price, membership_discount_price)
    VALUES (
    #{totalOrderPrice},
    #{membershipDiscountPrice}
    )
    </script>
    """)
    void insert(Order order);
}
