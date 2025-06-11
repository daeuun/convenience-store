package com.store.api.order.model;

import lombok.Getter;
import org.apache.ibatis.type.Alias;

@Getter
@Alias("Order")
public class Order {

    public static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    public static final double MEMBERSHIP_DISCOUNT_LIMIT = 8_000;

    private Long id;
    private long membershipDiscountPrice;
    private long totalOrderPrice;

    private Order(int membershipDiscountPrice, long totalOrderPrice) {
        this.membershipDiscountPrice = membershipDiscountPrice;
        this.totalOrderPrice = totalOrderPrice;
    }

    public static Order of(int membershipDiscountPrice, long totalOrderPrice) {
        return new Order(membershipDiscountPrice, totalOrderPrice);
    }
}

