package com.store.api.product.model;

import lombok.Getter;

@Getter
public class Product {
    private final Long id;
    private final String name;
    private final long price;

    public Product(Long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
