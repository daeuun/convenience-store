package store.product;

import store.promotion.PromotionDTO;

public record SaleProductDTO(
        ProductDTO product,
        int regularStock,
        int promotionStock,
        PromotionDTO promotion
) {
}