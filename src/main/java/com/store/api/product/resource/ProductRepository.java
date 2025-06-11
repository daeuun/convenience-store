package com.store.api.product.resource;

import com.store.api.common.exception.ResourceNotFoundException;
import com.store.api.product.model.Product;
import com.store.api.product.model.ProductSearchParam;
import com.store.api.product.model.ProductUpdateRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    private final ProductMapper productMapper;

    public ProductRepository(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> findAll(ProductSearchParam productSearchParam) {
        return productMapper.selectAll(productSearchParam);
    }

    public Product findByProductId(Long productId) {
        return Optional.ofNullable(productMapper.selectById(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }

    public void update(Long productId, ProductUpdateRequest request) {
        productMapper.update(productId, request);
    }

    public void delete(Long productId) {
        productMapper.delete(productId);
    }
}
