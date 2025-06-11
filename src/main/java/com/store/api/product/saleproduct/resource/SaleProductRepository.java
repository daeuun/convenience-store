package com.store.api.product.saleproduct.resource;

import com.store.api.common.exception.ResourceNotFoundException;
import com.store.api.product.saleproduct.model.SaleProduct;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SaleProductRepository {

    private final SaleProductMapper saleProductMapper;

    public SaleProductRepository(SaleProductMapper saleProductMapper) {
        this.saleProductMapper = saleProductMapper;
    }

    public SaleProduct findByProductId(Long productId) {
        return Optional.ofNullable(saleProductMapper.selectById(productId))
                .orElseThrow(() -> new ResourceNotFoundException("SaleProduct", productId));
    }

    public void decreaseRegularStock(int quantity, Long productId) {
        int updated = saleProductMapper.updateRegularStock(quantity, productId);
        if (updated == 0) {
            throw new IllegalStateException("Fail decrease regular stock");
        }
    }

    public void decreasePromotionStock(int quantity, Long productId) {
        int updated = saleProductMapper.updatePromotionStock(quantity, productId);
        if (updated == 0) {
            throw new IllegalStateException("Fail decrease promotion stock");
        }
    }
}
