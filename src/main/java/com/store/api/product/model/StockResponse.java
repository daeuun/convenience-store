package com.store.api.product.model;

import com.store.api.product.saleproduct.model.SaleProduct;
import lombok.Getter;

@Getter
public class StockResponse {
    private final Long saleProductId;
    private final int regularStock;
    private final int promotionStock;

    private StockResponse(Long saleProductId, int regularStock, int promotionStock) {
        this.saleProductId = saleProductId;
        this.regularStock = regularStock;
        this.promotionStock = promotionStock;
    }

    public static StockResponse from(SaleProduct saleProduct) {
        return new StockResponse(
                saleProduct.getId(),
                saleProduct.getRegularStock(),
                saleProduct.getPromotionStock()
        );
    }
}
