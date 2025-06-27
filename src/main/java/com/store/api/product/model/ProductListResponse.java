package com.store.api.product.model;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductListResponse {
    private List<ProductResponse> products;

    public ProductListResponse(List<ProductResponse> products) {
        this.products = products;
    }

    public static ProductListResponse from(List<ProductResponse> responses) {
        return new ProductListResponse(responses);
    }
}
