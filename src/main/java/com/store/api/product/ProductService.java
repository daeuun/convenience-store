package com.store.api.product;

import com.store.api.common.exception.ExceptionCode;
import com.store.api.order.model.OrderItem;
import com.store.api.product.model.Product;
import com.store.api.product.model.ProductListResponse;
import com.store.api.product.model.ProductResponse;
import com.store.api.product.model.ProductSearchParam;
import com.store.api.product.model.ProductUpdateRequest;
import com.store.api.product.model.StockResponse;
import com.store.api.product.resource.ProductRepository;
import com.store.api.product.saleproduct.model.SaleProduct;
import com.store.api.product.saleproduct.resource.SaleProductRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final SaleProductRepository saleProductRepository;
    private final RedissonClient redissonClient;

    public ProductService(ProductRepository productRepository,
                          SaleProductRepository saleProductRepository,
                          RedissonClient redissonClient
    ) {
        this.productRepository = productRepository;
        this.saleProductRepository = saleProductRepository;
        this.redissonClient = redissonClient;
    }

    @Transactional(readOnly = true)
    public ProductListResponse getProducts(ProductSearchParam searchParam) {
        List<Product> products = productRepository.findBySearchParam(searchParam);
        List<ProductResponse> responses = products.stream().map(ProductResponse::from).toList();
        return ProductListResponse.from(responses);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "product:detail", key = "#productId")
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public StockResponse getStocks(Long productId) {
        SaleProduct saleProduct = saleProductRepository.findByProductId(productId);
        return StockResponse.from(saleProduct);
    }

    @Transactional
    public void decreaseStock(OrderItem orderItem) {
        String lockKey = "lock:product:" + orderItem.getProductId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean hasLock = false;
        try {
            hasLock = lock.tryLock(3, 1, TimeUnit.SECONDS);
            if (!hasLock) {
                throw new IllegalStateException("Lock 획득 실패 - productId: " + orderItem.getProductId());
            }
            SaleProduct saleProduct = saleProductRepository.findByProductId(orderItem.getProductId());
            int regularQuantity = orderItem.getQuantity() - orderItem.getPromotionQuantity();
            int promotionQuantity = orderItem.getPromotionQuantity();
            if (regularQuantity > 0) {
                validateStockInsufficient(saleProduct.getRegularStock(), regularQuantity);
            } else if (promotionQuantity > 0) {
                validateStockInsufficient(saleProduct.getPromotionStock(), promotionQuantity);
            } else {
                throw new IllegalArgumentException("Invalid order item quantity");
            }
            saleProductRepository.decreaseRegularStock(regularQuantity, saleProduct.getProductId());
            saleProductRepository.decreasePromotionStock(promotionQuantity, saleProduct.getProductId());
        } catch (Exception e) {
            throw new RuntimeException("Fail acquire lock ", e);
        } finally {
            if (hasLock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void validateStockInsufficient(int availableStock, int requiredQuantity) {
        if (availableStock <= 0 || availableStock < requiredQuantity) {
            throw new IllegalArgumentException(ExceptionCode.OUT_OF_STOCK.message());
        }
    }

    @CacheEvict(value = "product:detail", key = "#productId")
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findByProductId(productId);
        productRepository.update(product.getId(), request);
        Product updated = productRepository.findByProductId(productId);
        return ProductResponse.from(updated);
    }

    @CacheEvict(value = "product:detail", key = "#productId")
    public void deleteProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        productRepository.delete(product.getId());
    }
}
