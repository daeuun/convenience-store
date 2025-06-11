package com.store.api.product.saleproduct.model;

import com.store.api.product.model.Product;
import com.store.api.promotion.model.Promotion;

public class SaleProductResponse {
    private final Long productId;
    private final int regularStock;
    private final int promotionStock;
    private final Long promotionId;

    public SaleProductResponse(Long productId, int regularStock, int promotionStock, Long promotionId) {
        this.productId = productId;
        this.regularStock = regularStock;
        this.promotionStock = promotionStock;
        this.promotionId = promotionId;
    }

    private static SaleProductResponse from(SaleProduct saleProduct) {
        return new SaleProductResponse(
                saleProduct.getProductId(),
                saleProduct.getRegularStock(),
                saleProduct.getPromotionStock(),
                saleProduct.getPromotionId()
        );
    }
}
