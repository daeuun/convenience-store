package com.store.api.product.saleproduct.model;

import lombok.Getter;

@Getter
public class SaleProduct {
    private final Long id;
    private final Long productId;
    private final int regularStock;
    private final int promotionStock;
    private final Long promotionId;

    private SaleProduct(Long id, Long productId, int regularStock, int promotionStock, Long promotionId) {
        this.id = id;
        this.productId = productId;
        this.regularStock = regularStock;
        this.promotionStock = promotionStock;
        this.promotionId = promotionId;
    }

    public static SaleProduct of(Long id, Long productId, int regularStock, int promotionStock, Long promotionId) {
        return new SaleProduct(id, productId, regularStock, promotionStock, promotionId);
    }

}
