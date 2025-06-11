package com.store.api.product.model;

import lombok.Getter;

@Getter
public class ProductUpdateRequest {
    private String name;
    private long price;
}
